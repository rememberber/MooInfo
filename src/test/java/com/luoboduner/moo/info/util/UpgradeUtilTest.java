package com.luoboduner.moo.info.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for version comparison used by update checks.
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
}
