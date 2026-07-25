package com.luoboduner.moo.info.util;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateDownloadManagerTest {

    @Test
    public void pendingDirIsUnderMooInfoConfigHome() {
        File dir = UpdateDownloadManager.pendingDir();
        assertEquals(new File(SystemUtil.CONFIG_HOME, "pending-updates"), dir);
        assertTrue(dir.getAbsolutePath().contains(".MooInfo"));
    }

    @Test
    public void buildOpenCommandMatchesCurrentOs() {
        String path = "/tmp/MooInfo-update.dmg";
        String[] command = UpdateDownloadManager.buildOpenCommand(path);
        if (SystemUtil.isMacOs()) {
            assertArrayEquals(new String[]{"open", path}, command);
        } else if (SystemUtil.isWindowsOs()) {
            assertArrayEquals(new String[]{"cmd", "/c", "start", "", path}, command);
        } else {
            assertArrayEquals(new String[]{"xdg-open", path}, command);
        }
    }
}
