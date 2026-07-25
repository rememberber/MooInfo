package com.luoboduner.moo.info.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.bean.VersionSummary;
import com.luoboduner.moo.info.ui.UiConsts;
import com.luoboduner.moo.info.ui.dialog.UpdateInfoDialog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Upgrade tool class
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/08.
 */
@Slf4j
public class UpgradeUtil {
    private static final int CHECK_TIMEOUT_MS = 10_000;
    private static final AtomicBoolean MANUAL_CHECKING = new AtomicBoolean(false);

    private static int parseVersionIndex(Map<String, String> versionIndexMap, String version) {
        String index = versionIndexMap.get(version);
        if (StringUtils.isBlank(index)) {
            throw new IllegalStateException("Missing version index: " + version);
        }
        return Integer.parseInt(index);
    }

    static List<VersionSummary.Version> versionChangesAfter(VersionSummary versionSummary, String currentVersion) {
        Map<String, String> versionIndexMap = JSON.parseObject(versionSummary.getVersionIndex(), Map.class);
        int currentVersionIndex = parseVersionIndex(versionIndexMap, currentVersion);
        int latestVersionIndex = parseVersionIndex(versionIndexMap, versionSummary.getCurrentVersion());
        if (latestVersionIndex <= currentVersionIndex) {
            return List.of();
        }

        List<VersionSummary.Version> versionDetailList = versionSummary.getVersionDetailList();
        if (versionDetailList == null) {
            throw new IllegalStateException("Missing version detail list");
        }
        List<VersionSummary.Version> changes = new ArrayList<>();
        for (VersionSummary.Version version : versionDetailList) {
            int versionIndex = parseVersionIndex(versionIndexMap, version.getVersion());
            if (versionIndex > currentVersionIndex && versionIndex <= latestVersionIndex) {
                changes.add(version);
            }
        }
        if (changes.size() != latestVersionIndex - currentVersionIndex) {
            throw new IllegalStateException("Incomplete version notes: "
                    + currentVersion + " -> " + versionSummary.getCurrentVersion());
        }
        changes.sort(Comparator.comparingInt(version -> parseVersionIndex(versionIndexMap, version.getVersion())));
        return changes;
    }

    /**
     * Check for updates. Network I/O runs off the EDT; dialogs are shown on the EDT.
     *
     * @param initCheck true for automatic startup checks (silent when already latest);
     *                  false for manual checks (always show a result)
     */
    public static void checkUpdate(boolean initCheck) {
        if (!initCheck && !MANUAL_CHECKING.compareAndSet(false, true)) {
            return;
        }
        ThreadUtil.execute(() -> {
            try {
                doCheckUpdate(initCheck);
            } catch (Exception e) {
                log.error("Check for update failed", e);
                if (!initCheck) {
                    showMessage("Check for timeouts, follow GitHub Release!", "Network error");
                }
            } finally {
                if (!initCheck) {
                    MANUAL_CHECKING.set(false);
                }
            }
        });
    }

    private static void doCheckUpdate(boolean initCheck) {
        String currentVersion = UiConsts.APP_VERSION;

        String versionSummaryJsonContent;
        try {
            versionSummaryJsonContent = HttpUtil.get(UiConsts.CHECK_VERSION_URL, CHECK_TIMEOUT_MS);
        } catch (Exception e) {
            log.error("Check for update network request failed", e);
            if (!initCheck) {
                showMessage("Check for timeouts, follow GitHub Release!", "Network error");
            }
            return;
        }
        if (StringUtils.isEmpty(versionSummaryJsonContent) || versionSummaryJsonContent.contains("404: Not Found")) {
            if (!initCheck) {
                showMessage("Check for timeouts, follow GitHub Release!", "Network error");
            }
            return;
        }
        versionSummaryJsonContent = versionSummaryJsonContent.replace("\n", "");

        VersionSummary versionSummary = JSON.parseObject(versionSummaryJsonContent, VersionSummary.class);
        String newVersion = versionSummary.getCurrentVersion();
        List<VersionSummary.Version> versionChanges;
        try {
            versionChanges = versionChangesAfter(versionSummary, currentVersion);
        } catch (IllegalStateException | NumberFormatException e) {
            log.error("Invalid version metadata while checking for updates", e);
            if (!initCheck) {
                showMessage("Failed to parse update information.", "Failure");
            }
            return;
        }

        if (!versionChanges.isEmpty()) {
            if (initCheck && App.config.isAutoDownloadUpdate()) {
                UpdateDownloadManager.getInstance().startSilentDownload(
                        newVersion, buildVersionChangesHtml(versionChanges, null));
                return;
            }

            String html = buildVersionChangesHtml(versionChanges, "Surprise the new version! Download it now?");
            SwingUtilities.invokeLater(() -> {
                UpdateInfoDialog updateInfoDialog = new UpdateInfoDialog();
                updateInfoDialog.setHtmlText(html);
                updateInfoDialog.setNewVersion(newVersion);
                updateInfoDialog.pack();
                updateInfoDialog.setVisible(true);
            });
        } else if (!initCheck) {
            showMessage("It's the latest version!", "Congratulations");
        }
    }

    private static void showMessage(String message, String title) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(App.mainFrame, message, title, JOptionPane.INFORMATION_MESSAGE));
    }

    static String buildVersionChangesHtml(List<VersionSummary.Version> versionChanges, String title) {
        StringBuilder versionLogBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(title)) {
            versionLogBuilder.append("<h1>").append(title).append("</h1>");
        }
        for (VersionSummary.Version version : versionChanges) {
            versionLogBuilder.append("<h2>").append(version.getVersion()).append("</h2>");
            versionLogBuilder.append("<b>").append(version.getTitle()).append("</b><br/>");
            versionLogBuilder.append("<p>").append(version.getLog().replaceAll("\\n", "</p><p>")).append("</p>");
        }
        return versionLogBuilder.toString();
    }

    /**
     * Smooth upgrade for local data/config migrations between installed versions.
     */
    public static void smoothUpgrade() {
        String currentVersion = UiConsts.APP_VERSION;
        String beforeVersion = App.config.getBeforeVersion();

        if (compareVersion(currentVersion, beforeVersion) <= 0) {
            return;
        }

        log.info("Smooth upgrade begins");

        String versionSummaryJsonContent = FileUtil.readString(
                UiConsts.class.getResource("/version_summary.json"), CharsetUtil.UTF_8);
        versionSummaryJsonContent = versionSummaryJsonContent.replace("\n", "");
        VersionSummary versionSummary = JSON.parseObject(versionSummaryJsonContent, VersionSummary.class);
        Map<String, String> versionIndexMap = JSON.parseObject(versionSummary.getVersionIndex(), Map.class);
        Integer currentVersionIndex = parseVersionIndexOrNull(versionIndexMap, currentVersion);
        Integer beforeVersionIndex = parseVersionIndexOrNull(versionIndexMap, beforeVersion);
        if (currentVersionIndex == null || beforeVersionIndex == null) {
            log.error("Smooth upgrade aborted: missing version index. before={}, current={}",
                    beforeVersion, currentVersion);
            App.config.setBeforeVersion(currentVersion);
            App.config.save();
            return;
        }
        log.info("Older version {}", beforeVersion);
        log.info("Current version {}", currentVersion);
        beforeVersionIndex++;
        for (int i = beforeVersionIndex; i <= currentVersionIndex; i++) {
            log.info("Update the version index {} begin", i);
            upgrade(i);
            log.info("Update the version index {} finished", i);
        }

        App.config.setBeforeVersion(currentVersion);
        App.config.save();
        log.info("Smooth upgrade ends");
    }

    /**
     * Compare semantic-like versions (e.g. 1.1.9 vs 1.1.10).
     *
     * @return negative if a &lt; b, zero if equal, positive if a &gt; b
     */
    static int compareVersion(String a, String b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        String[] aParts = a.split("\\.");
        String[] bParts = b.split("\\.");
        int len = Math.max(aParts.length, bParts.length);
        for (int i = 0; i < len; i++) {
            int aNum = i < aParts.length ? parseVersionPart(aParts[i]) : 0;
            int bNum = i < bParts.length ? parseVersionPart(bParts[i]) : 0;
            if (aNum != bNum) {
                return Integer.compare(aNum, bNum);
            }
        }
        return 0;
    }

    private static int parseVersionPart(String part) {
        if (StringUtils.isEmpty(part)) {
            return 0;
        }
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < part.length(); i++) {
            char c = part.charAt(i);
            if (Character.isDigit(c)) {
                digits.append(c);
            } else {
                break;
            }
        }
        if (digits.length() == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(digits.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static Integer parseVersionIndexOrNull(Map<String, String> versionIndexMap, String version) {
        if (versionIndexMap == null || StringUtils.isEmpty(version)) {
            return null;
        }
        String index = versionIndexMap.get(version);
        if (StringUtils.isEmpty(index)) {
            return null;
        }
        try {
            return Integer.parseInt(index);
        } catch (NumberFormatException e) {
            log.error("Invalid version index for {}: {}", version, index);
            return null;
        }
    }

    private static void upgrade(int versionIndex) {
        log.info("Start with the upgrade script, version index:{}", versionIndex);
        switch (versionIndex) {
            case 21:
                break;
            default:
        }
        log.info("The upgrade script ends, the version index:{}", versionIndex);
    }
}
