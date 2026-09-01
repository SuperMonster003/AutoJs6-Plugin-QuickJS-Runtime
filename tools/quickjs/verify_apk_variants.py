#!/usr/bin/env python3
"""Verify QuickJS APK inventory, native ABI contents, and 16 KB alignment."""

from __future__ import annotations

import argparse
import struct
import sys
import zipfile
from pathlib import Path


EXPECTED_VARIANTS = {
    "arm64-v8a": {"arm64-v8a"},
    "armeabi-v7a": {"armeabi-v7a"},
    "x86_64": {"x86_64"},
    "x86": {"x86"},
    "universal": {"arm64-v8a", "armeabi-v7a", "x86_64", "x86"},
}
EXPECTED_NATIVE_LIBRARY = "libautojs_quickjs_runtime.so"
EXPECTED_ELF_MACHINES = {
    "arm64-v8a": (2, 183),
    "armeabi-v7a": (1, 40),
    "x86_64": (2, 62),
    "x86": (1, 3),
}
FORBIDDEN_NATIVE_LIBRARIES = {"libc++_shared.so", "libquickjs.so", "libquickjs-libc.so"}
REQUIRED_DEX_MARKERS = {
    b"Lio/github/supermonster003/autojs6/plugin/quickjs/runtime/NativeQuickJsRuntime;": (
        "NativeQuickJsRuntime class descriptor"
    ),
    b"nativeExecute": "NativeQuickJsRuntime.nativeExecute JNI method",
    b"nativeCancel": "NativeQuickJsRuntime.nativeCancel JNI method",
}
PT_LOAD = 1
PT_GNU_RELRO = 0x6474E552
MINIMUM_ALIGNMENT = 16 * 1024
ZIP_LOCAL_HEADER = struct.Struct("<IHHHHHIIIHH")
ZIP_LOCAL_HEADER_SIGNATURE = 0x04034B50


class VerificationError(Exception):
    """Raised when an APK or embedded ELF violates the release contract."""


def unpack_from(byte_order: str, format_code: str, data: bytes, offset: int) -> tuple[int, ...]:
    try:
        return struct.unpack_from(byte_order + format_code, data, offset)
    except struct.error as error:
        raise VerificationError(f"Truncated ELF structure at offset {offset}: {error}") from None


def verify_elf(data: bytes, label: str, abi: str) -> int:
    if len(data) < 64 or data[:4] != b"\x7fELF":
        raise VerificationError(f"Not a valid ELF shared library: {label}")

    elf_class = data[4]
    byte_order_marker = data[5]
    if byte_order_marker == 1:
        byte_order = "<"
    elif byte_order_marker == 2:
        byte_order = ">"
    else:
        raise VerificationError(f"Unsupported ELF byte order in {label}: {byte_order_marker}")

    machine = unpack_from(byte_order, "H", data, 18)[0]
    expected_class, expected_machine = EXPECTED_ELF_MACHINES[abi]
    if (elf_class, machine) != (expected_class, expected_machine):
        raise VerificationError(
            f"ELF architecture mismatch in {label}: expected class={expected_class} "
            f"machine={expected_machine}, actual class={elf_class} machine={machine}",
        )

    if elf_class == 1:
        header_offset = unpack_from(byte_order, "I", data, 28)[0]
        entry_size = unpack_from(byte_order, "H", data, 42)[0]
        entry_count = unpack_from(byte_order, "H", data, 44)[0]
        minimum_entry_size = 32

        def program_header(index: int) -> tuple[int, int, int]:
            offset = header_offset + index * entry_size
            program_type = unpack_from(byte_order, "I", data, offset)[0]
            file_offset = unpack_from(byte_order, "I", data, offset + 4)[0]
            virtual_address = unpack_from(byte_order, "I", data, offset + 8)[0]
            alignment = unpack_from(byte_order, "I", data, offset + 28)[0]
            return program_type, file_offset - virtual_address, alignment

    elif elf_class == 2:
        header_offset = unpack_from(byte_order, "Q", data, 32)[0]
        entry_size = unpack_from(byte_order, "H", data, 54)[0]
        entry_count = unpack_from(byte_order, "H", data, 56)[0]
        minimum_entry_size = 56

        def program_header(index: int) -> tuple[int, int, int]:
            offset = header_offset + index * entry_size
            program_type = unpack_from(byte_order, "I", data, offset)[0]
            file_offset = unpack_from(byte_order, "Q", data, offset + 8)[0]
            virtual_address = unpack_from(byte_order, "Q", data, offset + 16)[0]
            alignment = unpack_from(byte_order, "Q", data, offset + 48)[0]
            return program_type, file_offset - virtual_address, alignment

    else:
        raise VerificationError(f"Unsupported ELF class in {label}: {elf_class}")

    if entry_size < minimum_entry_size:
        raise VerificationError(f"Invalid ELF program header size in {label}: {entry_size}")
    if header_offset + entry_size * entry_count > len(data):
        raise VerificationError(f"ELF program headers exceed file bounds in {label}")

    load_alignments: list[int] = []
    has_relro = False
    for index in range(entry_count):
        program_type, offset_delta, alignment = program_header(index)
        if program_type == PT_LOAD:
            if alignment < MINIMUM_ALIGNMENT:
                raise VerificationError(
                    f"ELF LOAD alignment is below 16 KB in {label}: 0x{alignment:x}",
                )
            if offset_delta % alignment != 0:
                raise VerificationError(
                    f"ELF LOAD offset/vaddr congruence is invalid in {label}: "
                    f"delta={offset_delta}, align=0x{alignment:x}",
                )
            load_alignments.append(alignment)
        elif program_type == PT_GNU_RELRO:
            has_relro = True

    if not load_alignments:
        raise VerificationError(f"ELF contains no LOAD segments: {label}")
    if not has_relro:
        raise VerificationError(f"ELF is missing GNU_RELRO: {label}")
    return min(load_alignments)


def zip_data_offset(apk_file, info: zipfile.ZipInfo, label: str) -> int:
    apk_file.seek(info.header_offset)
    header = apk_file.read(ZIP_LOCAL_HEADER.size)
    if len(header) != ZIP_LOCAL_HEADER.size:
        raise VerificationError(f"Truncated ZIP local header for {label}")
    fields = ZIP_LOCAL_HEADER.unpack(header)
    if fields[0] != ZIP_LOCAL_HEADER_SIGNATURE:
        raise VerificationError(f"Invalid ZIP local header signature for {label}")
    filename_length, extra_length = fields[-2:]
    return info.header_offset + ZIP_LOCAL_HEADER.size + filename_length + extra_length


def verify_apk(path: Path, expected_abis: set[str]) -> tuple[set[str], int, int]:
    try:
        with path.open("rb") as apk_file, zipfile.ZipFile(apk_file) as archive:
            corrupt_entry = archive.testzip()
            if corrupt_entry is not None:
                raise VerificationError(f"Corrupt ZIP entry in {path.name}: {corrupt_entry}")

            native_infos = {
                info.filename: info
                for info in archive.infolist()
                if info.filename.startswith("lib/") and info.filename.endswith(".so")
            }
            expected_entries = {
                f"lib/{abi}/{EXPECTED_NATIVE_LIBRARY}"
                for abi in expected_abis
            }
            if set(native_infos) != expected_entries:
                raise VerificationError(
                    f"Unexpected native inventory in {path.name}: "
                    f"missing={sorted(expected_entries - set(native_infos))}, "
                    f"unexpected={sorted(set(native_infos) - expected_entries)}",
                )
            forbidden = sorted(
                name for name in native_infos if name.rsplit("/", 1)[-1] in FORBIDDEN_NATIVE_LIBRARIES
            )
            if forbidden:
                raise VerificationError(f"Forbidden native libraries in {path.name}: {forbidden}")

            minimum_elf_alignment = 1 << 63
            minimum_zip_alignment = 1 << 63
            for name, info in native_infos.items():
                if info.compress_type != zipfile.ZIP_STORED:
                    raise VerificationError(f"Native library is compressed in {path.name}: {name}")
                data_offset = zip_data_offset(apk_file, info, f"{path.name}!/{name}")
                if data_offset % MINIMUM_ALIGNMENT != 0:
                    raise VerificationError(
                        f"Native ZIP data is not 16 KB aligned in {path.name}: "
                        f"{name} offset=0x{data_offset:x}",
                    )
                minimum_zip_alignment = min(minimum_zip_alignment, data_offset & -data_offset)
                abi = name.split("/", 2)[1]
                minimum_elf_alignment = min(
                    minimum_elf_alignment,
                    verify_elf(archive.read(name), f"{path.name}!/{name}", abi),
                )

            dex_entries = sorted(
                name for name in archive.namelist()
                if name.startswith("classes") and name.endswith(".dex")
            )
            if not dex_entries:
                raise VerificationError(f"APK contains no DEX files: {path.name}")
            dex_payload = b"".join(archive.read(name) for name in dex_entries)
            for marker, description in REQUIRED_DEX_MARKERS.items():
                if marker not in dex_payload:
                    raise VerificationError(f"DEX output is missing {description} in {path.name}")

            actual_abis = {name.split("/", 2)[1] for name in native_infos}
            return actual_abis, minimum_elf_alignment, minimum_zip_alignment
    except zipfile.BadZipFile:
        raise VerificationError(f"Not a valid APK ZIP archive: {path}") from None


def expected_apks(build_type: str, unsigned: bool = False) -> dict[str, set[str]]:
    if unsigned and build_type != "release":
        raise VerificationError("Unsigned APK naming is supported only for release builds")
    suffix = f"{build_type}-unsigned" if unsigned else build_type
    return {
        f"app-{variant}-{suffix}.apk": abis
        for variant, abis in EXPECTED_VARIANTS.items()
    }


def verify_instrumentation_apk(path: Path) -> None:
    if not path.is_file():
        raise VerificationError(f"Instrumentation APK is missing: {path}")
    try:
        with zipfile.ZipFile(path) as archive:
            corrupt_entry = archive.testzip()
            if corrupt_entry is not None:
                raise VerificationError(f"Corrupt instrumentation APK entry: {corrupt_entry}")
            native_entries = sorted(
                name for name in archive.namelist()
                if name.startswith("lib/") and name.endswith(".so")
            )
            if native_entries:
                raise VerificationError(
                    f"Instrumentation APK must not package native libraries: {native_entries}",
                )
            if not any(
                name.startswith("classes") and name.endswith(".dex")
                for name in archive.namelist()
            ):
                raise VerificationError(f"Instrumentation APK contains no DEX: {path.name}")
    except zipfile.BadZipFile:
        raise VerificationError(f"Not a valid instrumentation APK ZIP archive: {path}") from None
    print(f"INSTRUMENTATION_APK_OK {path.name} native_libraries=0")


def verify(directory: Path, build_type: str = "debug", unsigned: bool = False) -> None:
    if not directory.is_dir():
        raise VerificationError(f"APK output directory is missing: {directory}")
    expected_variants = expected_apks(build_type, unsigned)
    actual = {path.name for path in directory.glob("*.apk") if path.is_file()}
    expected = set(expected_variants)
    if actual != expected:
        raise VerificationError(
            f"Unexpected APK inventory: missing={sorted(expected - actual)}, "
            f"unexpected={sorted(actual - expected)}",
        )

    for filename, expected_abis in expected_variants.items():
        path = directory / filename
        actual_abis, elf_alignment, zip_alignment = verify_apk(path, expected_abis)
        if actual_abis != expected_abis:
            raise VerificationError(
                f"Unexpected ABI set in {filename}: "
                f"expected={sorted(expected_abis)}, actual={sorted(actual_abis)}",
            )
        print(
            f"APK_OK {filename} abis={','.join(sorted(actual_abis))} "
            f"native={EXPECTED_NATIVE_LIBRARY} min_elf_align=0x{elf_alignment:x} "
            f"min_zip_data_align=0x{zip_alignment:x} bytes={path.stat().st_size}",
        )


def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("directory", type=Path, help="directory containing split APK outputs")
    parser.add_argument(
        "build_type",
        nargs="?",
        choices=("debug", "release"),
        default="debug",
        help="APK build type, default debug",
    )
    parser.add_argument("--instrumentation-apk", type=Path)
    parser.add_argument("--unsigned", action="store_true")
    return parser.parse_args()


def main() -> int:
    arguments = parse_arguments()
    try:
        verify(arguments.directory.resolve(), arguments.build_type, arguments.unsigned)
        if arguments.instrumentation_apk is not None:
            verify_instrumentation_apk(arguments.instrumentation_apk.resolve())
    except VerificationError as error:
        print(f"APK_ERROR {error}", file=sys.stderr)
        return 1
    print(f"APK_SET_OK variants={len(EXPECTED_VARIANTS)} build_type={arguments.build_type}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
