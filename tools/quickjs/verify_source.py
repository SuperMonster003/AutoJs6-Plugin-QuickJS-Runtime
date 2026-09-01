#!/usr/bin/env python3
"""Verify the pinned QuickJS source inventory without network access."""

from __future__ import annotations

import hashlib
import json
from pathlib import Path
import sys


REQUIRED_UPSTREAM = {
    "name": "QuickJS",
    "version": "2026-06-04",
    "commit": "3d5e064e9dd67c70f7962836505a7fa067bf0a4e",
    "archiveUrl": "https://bellard.org/quickjs/quickjs-2026-06-04.tar.xz",
    "archiveBytes": 621500,
    "archiveSha256": "b376e839b322978313d929fd20663b11ba58b75df5a46c126dd19ea2fa70ad2a",
    "license": "MIT",
}


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def verify(lock_path: Path, repository_root: Path) -> list[str]:
    errors: list[str] = []
    data = json.loads(lock_path.read_text(encoding="utf-8"))
    if data.get("schemaVersion") != 1:
        errors.append("source lock schemaVersion must be 1")
    if data.get("upstream") != REQUIRED_UPSTREAM:
        errors.append("source lock upstream metadata does not match the pinned release")

    vendor_root_value = data.get("vendorRoot")
    if not isinstance(vendor_root_value, str) or not vendor_root_value:
        return errors + ["source lock vendorRoot is missing"]
    vendor_root = (repository_root / vendor_root_value).resolve()
    try:
        vendor_root.relative_to(repository_root.resolve())
    except ValueError:
        return errors + ["source lock vendorRoot escapes the repository"]

    records = data.get("files")
    if not isinstance(records, list) or not records:
        return errors + ["source lock files must be a non-empty list"]
    expected_names: set[str] = set()
    for record in records:
        if not isinstance(record, dict):
            errors.append("source lock contains a non-object file record")
            continue
        relative = record.get("path")
        if not isinstance(relative, str) or not relative or Path(relative).is_absolute():
            errors.append(f"invalid source path: {relative!r}")
            continue
        source = (vendor_root / relative).resolve()
        try:
            source.relative_to(vendor_root)
        except ValueError:
            errors.append(f"source path escapes vendor root: {relative}")
            continue
        if relative in expected_names:
            errors.append(f"duplicate source path: {relative}")
            continue
        expected_names.add(relative)
        if not source.is_file():
            errors.append(f"missing vendored source: {relative}")
            continue
        actual_size = source.stat().st_size
        if actual_size != record.get("bytes"):
            errors.append(f"size mismatch for {relative}: {actual_size}")
        actual_hash = sha256(source)
        if actual_hash != record.get("sha256"):
            errors.append(f"SHA-256 mismatch for {relative}: {actual_hash}")

    actual_names = {
        path.relative_to(vendor_root).as_posix()
        for path in vendor_root.rglob("*")
        if path.is_file()
    }
    unexpected = sorted(actual_names - expected_names)
    missing = sorted(expected_names - actual_names)
    if unexpected:
        errors.append(f"unexpected vendored files: {', '.join(unexpected)}")
    if missing:
        errors.append(f"locked vendored files are absent: {', '.join(missing)}")
    return errors


def main() -> int:
    repository_root = Path(__file__).resolve().parents[2]
    lock_path = repository_root / "tools" / "quickjs" / "source.lock.json"
    errors = verify(lock_path, repository_root)
    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        return 1
    print("Verified pinned QuickJS 2026-06-04 source inventory")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
