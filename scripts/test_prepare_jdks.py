from __future__ import annotations

import tempfile
import unittest
from pathlib import Path

from scripts.prepare_jdks import (
    TARGETS,
    format_size,
    has_jmods,
    install_from_existing_java_home,
    install_jmods,
    locate_java_home,
    locate_jmods_dir,
    parse_targets,
)


class PrepareJdksTests(unittest.TestCase):
    def test_format_size_for_bytes(self) -> None:
        self.assertEqual(format_size(512), "512 B")

    def test_format_size_for_mebibytes(self) -> None:
        self.assertEqual(format_size(5 * 1024 * 1024), "5.0 MiB")

    def test_parse_targets_accepts_all(self) -> None:
        targets = parse_targets("all")
        self.assertEqual({target.key for target in targets}, set(TARGETS))

    def test_parse_targets_rejects_unknown_target(self) -> None:
        with self.assertRaises(ValueError):
            parse_targets("mac-x64,unknown")

    def test_locate_java_home_for_standard_layout(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            root = Path(tmp_dir)
            home = root / "jdk-25"
            (home / "bin").mkdir(parents=True)
            (home / "bin" / "java").write_text("", encoding="utf-8")
            self.assertEqual(locate_java_home(root), home)

    def test_locate_java_home_for_macos_layout(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            root = Path(tmp_dir)
            home = root / "temurin-25.jdk" / "Contents" / "Home"
            (home / "bin").mkdir(parents=True)
            (home / "bin" / "java").write_text("", encoding="utf-8")
            self.assertEqual(locate_java_home(root), home)

    def test_locate_jmods_dir_for_standard_layout(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            root = Path(tmp_dir)
            jmods = root / "jdk-25" / "jmods"
            jmods.mkdir(parents=True)
            (jmods / "java.base.jmod").write_text("", encoding="utf-8")
            self.assertEqual(locate_jmods_dir(root), jmods)

    def test_locate_jmods_dir_for_adoptium_archive_layout(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            root = Path(tmp_dir)
            jmods = root / "jdk-25.0.4.1+1-jmods"
            jmods.mkdir(parents=True)
            (jmods / "java.base.jmod").write_text("", encoding="utf-8")
            self.assertEqual(locate_jmods_dir(root), jmods)

    def test_locate_jmods_dir_for_macos_layout(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            root = Path(tmp_dir)
            jmods = root / "jdk-25.jdk" / "Contents" / "Home" / "jmods"
            jmods.mkdir(parents=True)
            (jmods / "java.base.jmod").write_text("", encoding="utf-8")
            self.assertEqual(locate_jmods_dir(root), jmods)

    def test_install_jmods_copies_modules_into_java_home(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            root = Path(tmp_dir)
            source = root / "extracted" / "jmods"
            source.mkdir(parents=True)
            (source / "java.base.jmod").write_text("module", encoding="utf-8")
            install_home = root / "home"
            install_home.mkdir()

            install_jmods(install_home, source)

            self.assertTrue(has_jmods(install_home))
            self.assertEqual((install_home / "jmods" / "java.base.jmod").read_text(encoding="utf-8"), "module")

    def test_install_from_existing_java_home(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            root = Path(tmp_dir)
            source_home = root / "source-home"
            (source_home / "bin").mkdir(parents=True)
            (source_home / "bin" / "java").write_text("", encoding="utf-8")
            destination_home = root / "jdks" / "mac" / "x64" / "home"

            install_from_existing_java_home(destination_home, source_home)

            self.assertTrue((destination_home / "bin" / "java").exists())


if __name__ == "__main__":
    unittest.main()

