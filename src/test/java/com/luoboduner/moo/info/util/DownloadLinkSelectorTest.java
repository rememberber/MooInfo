package com.luoboduner.moo.info.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DownloadLinkSelectorTest {

    @Test
    public void selectsAppleSiliconDownloadBeforeGenericMacDownload() {
        JSONObject links = JSON.parseObject("{"
                + "\"mac\":\"https://example.com/MooInfo.dmg\","
                + "\"macSilicon\":\"https://example.com/MooInfo-AppleSilicon.dmg\""
                + "}");

        String selected = DownloadLinkSelector.select("Mac OS X", "aarch64", links);

        assertEquals("https://example.com/MooInfo-AppleSilicon.dmg", selected);
    }

    @Test
    public void fallsBackToGenericMacDownloadWhenAppleSiliconDownloadIsMissing() {
        JSONObject links = JSON.parseObject("{"
                + "\"mac\":\"https://example.com/MooInfo.dmg\""
                + "}");

        String selected = DownloadLinkSelector.select("Mac OS X", "aarch64", links);

        assertEquals("https://example.com/MooInfo.dmg", selected);
    }

    @Test
    public void selectsGenericMacDownloadForIntelMac() {
        JSONObject links = JSON.parseObject("{"
                + "\"mac\":\"https://example.com/MooInfo.dmg\","
                + "\"macSilicon\":\"https://example.com/MooInfo-AppleSilicon.dmg\""
                + "}");

        String selected = DownloadLinkSelector.select("Mac OS X", "x86_64", links);

        assertEquals("https://example.com/MooInfo.dmg", selected);
    }
}
