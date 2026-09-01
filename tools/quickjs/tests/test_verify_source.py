from __future__ import annotations

import importlib.util
import json
from pathlib import Path
import tempfile
import unittest


MODULE_PATH = Path(__file__).resolve().parents[1] / "verify_source.py"
SPEC = importlib.util.spec_from_file_location("verify_source", MODULE_PATH)
assert SPEC and SPEC.loader
verify_source = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(verify_source)


class VerifySourceTest(unittest.TestCase):
    def test_repository_inventory_is_valid(self) -> None:
        root = Path(__file__).resolve().parents[3]
        lock = root / "tools" / "quickjs" / "source.lock.json"
        self.assertEqual([], verify_source.verify(lock, root))

    def test_tampered_file_is_rejected(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            vendor = root / "vendor"
            vendor.mkdir()
            source = vendor / "quickjs.c"
            source.write_bytes(b"tampered")
            lock = root / "lock.json"
            lock.write_text(
                json.dumps(
                    {
                        "schemaVersion": 1,
                        "upstream": verify_source.REQUIRED_UPSTREAM,
                        "vendorRoot": "vendor",
                        "files": [
                            {
                                "path": "quickjs.c",
                                "bytes": len(b"official-source"),
                                "sha256": "0" * 64,
                            }
                        ],
                    }
                ),
                encoding="utf-8",
            )
            errors = verify_source.verify(lock, root)
            self.assertTrue(any("size mismatch" in error for error in errors))
            self.assertTrue(any("SHA-256 mismatch" in error for error in errors))


if __name__ == "__main__":
    unittest.main()
