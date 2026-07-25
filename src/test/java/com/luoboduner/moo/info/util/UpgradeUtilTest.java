package com.luoboduner.moo.info.util;

import com.luoboduner.moo.info.bean.VersionSummary;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for version comparison and changelog range used by update checks.
 */
public class UpgradeUtilTest {

    @Test
    public void compareVersion_handlesMultiDigitPatch() {
        assertTrue(UpgradeUtil.compareVersion("1.1.10", "1.1.9") > 0);
        assertTrue(UpgradeUtil.compareVersion("1.1.9", "1.1.10") < 0);
        assertEquals(0, UpgradeUtil.compareVersion("1.1.4", "1.1.4"));
    }

    @Test
    public void compareVersion_handlesDifferentSegmentCounts() {
        assertTrue(UpgradeUtil.compareVersion("1.10.0", "1.9.0") > 0);
        assertTrue(UpgradeUtil.compareVersion("2.0", "1.9.9") > 0);
        assertTrue(UpgradeUtil.compareVersion("1.1", "1.1.0") == 0);
    }

    @Test
    public void versionChangesAfter_returnsOrderedNewerVersions() {
        VersionSummary summary = new VersionSummary();
        summary.setCurrentVersion("1.1.2");
        summary.setVersionIndex("{\"1.1.0\":\"1\",\"1.1.1\":\"2\",\"1.1.2\":\"3\"}");

        VersionSummary.Version v1 = new VersionSummary.Version();
        v1.setVersion("1.1.1");
        v1.setTitle("one");
        v1.setLog("log1");
        VersionSummary.Version v2 = new VersionSummary.Version();
        v2.setVersion("1.1.2");
        v2.setTitle("two");
        v2.setLog("log2");
        summary.setVersionDetailList(List.of(v1, v2));

        List<VersionSummary.Version> changes = UpgradeUtil.versionChangesAfter(summary, "1.1.0");
        assertEquals(2, changes.size());
        assertEquals("1.1.1", changes.get(0).getVersion());
        assertEquals("1.1.2", changes.get(1).getVersion());
    }

    @Test
    public void versionChangesAfter_rejectsIncompleteNotes() {
        VersionSummary summary = new VersionSummary();
        summary.setCurrentVersion("1.1.2");
        summary.setVersionIndex("{\"1.1.0\":\"1\",\"1.1.1\":\"2\",\"1.1.2\":\"3\"}");
        VersionSummary.Version v2 = new VersionSummary.Version();
        v2.setVersion("1.1.2");
        v2.setTitle("two");
        v2.setLog("log2");
        summary.setVersionDetailList(List.of(v2));

        assertThrows(IllegalStateException.class, () -> UpgradeUtil.versionChangesAfter(summary, "1.1.0"));
    }
}
