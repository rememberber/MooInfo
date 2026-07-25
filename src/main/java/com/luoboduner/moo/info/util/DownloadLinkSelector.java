package com.luoboduner.moo.info.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * Select the platform-specific installer URL from download_links.json.
 */
public class DownloadLinkSelector {

    public static String select(JSONObject links) {
        return select(System.getProperty("os.name"), System.getProperty("os.arch"), links);
    }

    static String select(String osName, String osArch, JSONObject links) {
        if (contains(osName, "Windows")) {
            return stringOrEmpty(links.getString("windows"));
        }

        if (contains(osName, "Mac")) {
            if ("aarch64".equals(osArch)) {
                String appleSiliconLink = stringOrEmpty(links.getString("macSilicon"));
                if (StringUtils.isNotEmpty(appleSiliconLink)) {
                    return appleSiliconLink;
                }
            }
            return stringOrEmpty(links.getString("mac"));
        }

        if (contains(osName, "Linux")) {
            return stringOrEmpty(links.getString("linux"));
        }

        return "";
    }

    private static boolean contains(String value, String searchText) {
        return value != null && value.contains(searchText);
    }

    private static String stringOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
