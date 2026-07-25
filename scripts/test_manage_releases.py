from __future__ import annotations

import json
import tempfile
import unittest
from pathlib import Path

from scripts.manage_releases import java_release_info, release_outputs


class ManageReleasesTests(unittest.TestCase):
    def test_java_release_requires_matching_versions(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            root = Path(tmp_dir)
            self.write_java_project(root, "1.2.3")
            info = java_release_info(root, "v1.2.3")
            self.assertEqual(info.title, "MooInfo 1.2.3")
            self.assertFalse(info.prerelease)
            self.assertEqual(release_outputs(info)["make_latest"], "true")
            with self.assertRaisesRegex(ValueError, "Java tag must be"):
                java_release_info(root, "v1.2.4")

    def test_java_release_rejects_missing_notes(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            root = Path(tmp_dir)
            self.write_java_project(root, "1.2.3", include_log=False)
            with self.assertRaisesRegex(ValueError, "missing log"):
                java_release_info(root, "v1.2.3")

    def write_java_project(self, root: Path, version: str, include_log: bool = True) -> None:
        (root / "pom.xml").write_text(
            f"""<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.luoboduner.moo.info</groupId>
  <artifactId>MooInfo</artifactId>
  <version>{version}</version>
  <name>MooInfo</name>
</project>
""",
            encoding="utf-8",
        )
        ui_path = root / "src/main/java/com/luoboduner/moo/info/ui/UiConsts.java"
        ui_path.parent.mkdir(parents=True, exist_ok=True)
        ui_path.write_text(f'public class UiConsts {{ public static final String APP_VERSION = "{version}"; }}\n', encoding="utf-8")

        summary_path = root / "src/main/resources/version_summary.json"
        summary_path.parent.mkdir(parents=True, exist_ok=True)
        detail = {"version": version, "title": "Test release"}
        if include_log:
            detail["log"] = "● fix something\n● improve something\n"
        summary_path.write_text(
            json.dumps({
                "currentVersion": version,
                "versionIndex": {"0.0.0": "0", version: "1"},
                "versionDetailList": [detail],
            }),
            encoding="utf-8",
        )


if __name__ == "__main__":
    unittest.main()
