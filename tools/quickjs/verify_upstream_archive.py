#!/usr/bin/env python3
"""Verify a downloaded official QuickJS archive against the source lock."""

from __future__ import annotations

import argparse
import hashlib
import json
import sys
import tarfile
from pathlib import Path, PurePosixPath


class ArchiveVerificationError(Exception):
    """Raised when the archive cannot reproduce the pinned vendored files."""


def sha256_bytes(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def verify_archive(archive_path: Path, lock_path: Path) -> None:
    lock = json.loads(lock_path.read_text(encoding="utf-8"))
    upstream = lock["upstream"]
    archive_bytes = archive_path.read_bytes()
    if len(archive_bytes) != upstream["archiveBytes"]:
        raise ArchiveVerificationError(
            f"archive size mismatch: expected={upstream['archiveBytes']} actual={len(archive_bytes)}",
        )
    actual_archive_hash = sha256_bytes(archive_bytes)
    if actual_archive_hash != upstream["archiveSha256"]:
        raise ArchiveVerificationError(
            f"archive SHA-256 mismatch: expected={upstream['archiveSha256']} "
            f"actual={actual_archive_hash}",
        )

    root = f"quickjs-{upstream['version']}"
    expected = {record["path"]: record for record in lock["files"]}
    seen: set[str] = set()
    try:
        with tarfile.open(archive_path, mode="r:xz") as archive:
            members = {member.name: member for member in archive.getmembers() if member.isfile()}
            for relative, record in expected.items():
                safe_relative = PurePosixPath(relative)
                if safe_relative.is_absolute() or ".." in safe_relative.parts:
                    raise ArchiveVerificationError(f"unsafe locked path: {relative}")
                member_name = f"{root}/{safe_relative.as_posix()}"
                member = members.get(member_name)
                if member is None:
                    raise ArchiveVerificationError(f"official archive is missing {member_name}")
                stream = archive.extractfile(member)
                if stream is None:
                    raise ArchiveVerificationError(f"unable to read {member_name}")
                data = stream.read()
                if len(data) != record["bytes"]:
                    raise ArchiveVerificationError(
                        f"size mismatch for {relative}: expected={record['bytes']} actual={len(data)}",
                    )
                digest = sha256_bytes(data)
                if digest != record["sha256"]:
                    raise ArchiveVerificationError(
                        f"SHA-256 mismatch for {relative}: expected={record['sha256']} actual={digest}",
                    )
                seen.add(relative)
    except (tarfile.TarError, OSError) as error:
        raise ArchiveVerificationError(f"unable to read QuickJS archive: {error}") from None

    if seen != set(expected):
        raise ArchiveVerificationError(
            f"archive comparison incomplete: missing={sorted(set(expected) - seen)}",
        )


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("archive", type=Path)
    parser.add_argument(
        "--lock",
        type=Path,
        default=Path(__file__).with_name("source.lock.json"),
    )
    arguments = parser.parse_args()
    try:
        verify_archive(arguments.archive.resolve(), arguments.lock.resolve())
    except (ArchiveVerificationError, KeyError, json.JSONDecodeError) as error:
        print(f"ARCHIVE_ERROR {error}", file=sys.stderr)
        return 1
    print("ARCHIVE_OK QuickJS 2026-06-04 selected files reproduce vendored bytes")
    return 0


if __name__ == "__main__":
    sys.exit(main())
