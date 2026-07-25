package com.luoboduner.moo.info.util;

import cn.hutool.core.io.FileUtil;
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
import java.util.List;
import java.util.Map;

/**
 * Upgrade tool class
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/08.
 */
@Slf4j
public class UpgradeUtil {

    public static void checkUpdate(boolean initCheck) {
        // current version
        String currentVersion = UiConsts.APP_VERSION;

        // Get information about the latest version from github (network I/O stays off EDT)
        String versionSummaryJsonContent = HttpUtil.get(UiConsts.CHECK_VERSION_URL);
        if (StringUtils.isEmpty(versionSummaryJsonContent) && !initCheck) {
            EdtUtil.run(() -> JOptionPane.showMessageDialog(App.mainFrame,
                    "Check for timeouts, follow GitHub Release!", "Network error",
                    JOptionPane.INFORMATION_MESSAGE));
            return;
        } else if (StringUtils.isEmpty(versionSummaryJsonContent) || versionSummaryJsonContent.contains("404: Not Found")) {
            return;
        }
        versionSummaryJsonContent = versionSummaryJsonContent.replace("\n", "");

        VersionSummary versionSummary = JSON.parseObject(versionSummaryJsonContent, VersionSummary.class);
        // The latest version
        String newVersion = versionSummary.getCurrentVersion();
        String versionIndexJsonContent = versionSummary.getVersionIndex();
        // Version index
        Map<String, String> versionIndexMap = JSON.parseObject(versionIndexJsonContent, Map.class);
        // list of version details
        List<VersionSummary.Version> versionDetailList = versionSummary.getVersionDetailList();

        if (compareVersion(newVersion, currentVersion) > 0) {
            // Version update log:
            StringBuilder versionLogBuilder = new StringBuilder("<h1>Surprise the new version! Download it now?</h1>");
            Integer currentVersionIndex = parseVersionIndex(versionIndexMap, currentVersion);
            if (currentVersionIndex != null && versionDetailList != null) {
                VersionSummary.Version version;
                for (int i = currentVersionIndex + 1; i < versionDetailList.size(); i++) {
                    version = versionDetailList.get(i);
                    versionLogBuilder.append("<h2>").append(version.getVersion()).append("</h2>");
                    versionLogBuilder.append("<b>").append(version.getTitle()).append("</b><br/>");
                    versionLogBuilder.append("<p>").append(version.getLog().replaceAll("\\n", "</p><p>")).append("</p>");
                }
            } else {
                log.warn("Missing version index for currentVersion={}, showing update dialog without changelog range",
                        currentVersion);
                versionLogBuilder.append("<p>New version: ").append(newVersion).append("</p>");
            }
            String versionLog = versionLogBuilder.toString();

            EdtUtil.run(() -> {
                UpdateInfoDialog updateInfoDialog = new UpdateInfoDialog();
                updateInfoDialog.setHtmlText(versionLog);
                updateInfoDialog.setNewVersion(newVersion);
                updateInfoDialog.pack();
                updateInfoDialog.setVisible(true);
            });
        } else {
            if (!initCheck) {
                EdtUtil.run(() -> JOptionPane.showMessageDialog(App.mainFrame,
                        "It's the latest version!", "Congratulations",
                        JOptionPane.INFORMATION_MESSAGE));
            }
        }
    }

    /**
     * Smooth upgrade
     * The version update scripts and sql methods involved are as idempotent as possible to avoid repeated upgrade operations due to unusual interruptions such as power failures and deaths during the upgrade process
     */
    public static void smoothUpgrade() {
        // Get the current version
        String currentVersion = UiConsts.APP_VERSION;
        // Get the before upgrade version
        String beforeVersion = App.config.getBeforeVersion();

        if (compareVersion(currentVersion, beforeVersion) <= 0) {
            // If both are consistent, no upgrade action is performed
            return;
        } else {
            log.info("Smooth upgrade begins");

            // Then take the index for both versions
            String versionSummaryJsonContent = FileUtil.readString(UiConsts.class.getResource("/version_summary.json"), CharsetUtil.UTF_8);
            versionSummaryJsonContent = versionSummaryJsonContent.replace("\n", "");
            VersionSummary versionSummary = JSON.parseObject(versionSummaryJsonContent, VersionSummary.class);
            String versionIndex = versionSummary.getVersionIndex();
            Map<String, String> versionIndexMap = JSON.parseObject(versionIndex, Map.class);
            Integer currentVersionIndex = parseVersionIndex(versionIndexMap, currentVersion);
            Integer beforeVersionIndex = parseVersionIndex(versionIndexMap, beforeVersion);
            if (currentVersionIndex == null || beforeVersionIndex == null) {
                log.error("Smooth upgrade aborted: missing version index. before={}, current={}",
                        beforeVersion, currentVersion);
                // Still advance beforeVersion so startup is not stuck on every launch
                App.config.setBeforeVersion(currentVersion);
                App.config.save();
                return;
            }
            log.info("Older version{}", beforeVersion);
            log.info("Current version{}", currentVersion);
            // Traverses the index range
            beforeVersionIndex++;
            for (int i = beforeVersionIndex; i <= currentVersionIndex; i++) {
                log.info("Update the version index {} begin", i);
                // Perform updates to each version index, from far to nearby time
                upgrade(i);
                log.info("Update the version index {} finished", i);
            }

            // If the upgrade is complete and successful, the version number prior to the upgrade is assigned to the current version
            App.config.setBeforeVersion(currentVersion);
            App.config.save();
            log.info("Smooth upgrade ends");
        }
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
        // Strip non-digit suffix like "1-SNAPSHOT" / "1rc1"
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

    private static Integer parseVersionIndex(Map<String, String> versionIndexMap, String version) {
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

    /**
     * Execute the upgrade script
     *
     * @param versionIndex Version index
     */
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
