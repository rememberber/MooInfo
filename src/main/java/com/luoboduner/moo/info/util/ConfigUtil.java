package com.luoboduner.moo.info.util;

/**
 * Configuration management
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/08.
 */
public class ConfigUtil extends ConfigBaseUtil {

    private static final ConfigUtil configUtil = new ConfigUtil();

    public static ConfigUtil getInstance() {
        return configUtil;
    }

    private ConfigUtil() {
        super();
    }

    private boolean autoCheckUpdate;

    private boolean defaultMaxWindow;

    private boolean unifiedBackground;

    private String beforeVersion;

    private String theme;

    private String font;

    private int fontSize;

    public boolean isAutoCheckUpdate() {
        return setting.getBool("autoCheckUpdate", "setting.common", true);
    }

    public void setAutoCheckUpdate(boolean autoCheckUpdate) {
        setting.putByGroup("autoCheckUpdate", "setting.common", String.valueOf(autoCheckUpdate));
    }

    public boolean isDefaultMaxWindow() {
        return setting.getBool("defaultMaxWindow", "setting.normal", false);
    }

    public void setDefaultMaxWindow(boolean defaultMaxWindow) {
        setting.putByGroup("defaultMaxWindow", "setting.normal", String.valueOf(defaultMaxWindow));
    }

    public boolean isUnifiedBackground() {
        return setting.getBool("unifiedBackground", "setting.normal", true);
    }

    public void setUnifiedBackground(boolean unifiedBackground) {
        setting.putByGroup("unifiedBackground", "setting.normal", String.valueOf(unifiedBackground));
    }

    public String getBeforeVersion() {
        return setting.getStr("beforeVersion", "setting.common", "0.0.0");
    }

    public void setBeforeVersion(String beforeVersion) {
        setting.putByGroup("beforeVersion", "setting.common", beforeVersion);
    }

    public String getTheme() {
        if (SystemUtil.isLinuxOs()) {
            return setting.getStr("theme", "setting.appearance", "System Default");
        } else {
            return setting.getStr("theme", "setting.appearance", "Dark purple");
        }
    }

    public void setTheme(String theme) {
        setting.putByGroup("theme", "setting.appearance", theme);
    }

    public String getFont() {
        if (SystemUtil.isLinuxOs()) {
            return setting.getStr("font", "setting.appearance", "Noto Sans CJK HK");
        } else {
            return setting.getStr("font", "setting.appearance", "Microsoft YaHei");
        }
    }

    public void setFont(String font) {
        setting.putByGroup("font", "setting.appearance", font);
    }

    public int getFontSize() {
        return setting.getInt("fontSize", "setting.appearance", 13);
    }

    public void setFontSize(int fontSize) {
        setting.putByGroup("fontSize", "setting.appearance", String.valueOf(fontSize));
    }

}
