from __future__ import annotations

import sys
import unittest
from pathlib import Path


SCRIPT_DIRECTORY = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(SCRIPT_DIRECTORY))

import verify_apk_variants  # noqa: E402


class VerifyApkVariantsTest(unittest.TestCase):
    def test_debug_inventory_contains_all_split_and_universal_packages(self) -> None:
        names = set(verify_apk_variants.expected_apks("debug"))
        self.assertEqual(
            {
                "app-arm64-v8a-debug.apk",
                "app-armeabi-v7a-debug.apk",
                "app-x86_64-debug.apk",
                "app-x86-debug.apk",
                "app-universal-debug.apk",
            },
            names,
        )

    def test_signed_release_uses_public_names(self) -> None:
        names = set(verify_apk_variants.expected_apks("release"))
        self.assertIn("app-arm64-v8a-release.apk", names)
        self.assertNotIn("app-arm64-v8a-release-unsigned.apk", names)

    def test_unsigned_release_uses_gradle_unsigned_names(self) -> None:
        names = set(verify_apk_variants.expected_apks("release", unsigned=True))
        self.assertIn("app-arm64-v8a-release-unsigned.apk", names)
        self.assertEqual(5, len(names))

    def test_unsigned_debug_is_rejected(self) -> None:
        with self.assertRaisesRegex(
            verify_apk_variants.VerificationError,
            "only for release builds",
        ):
            verify_apk_variants.expected_apks("debug", unsigned=True)


if __name__ == "__main__":
    unittest.main()
