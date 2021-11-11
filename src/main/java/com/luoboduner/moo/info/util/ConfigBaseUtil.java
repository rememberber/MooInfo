package com.luoboduner.moo.info.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;

import java.io.File;

/**
 * Base class of configuration management
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/08.
 */
public class ConfigBaseUtil {
    /**
     * path of the setting file
     */
    private String settingFilePath = SystemUtil.CONFIG_HOME + File.separator + "config" + File.separator + "config.setting";

    Setting setting;

    ConfigBaseUtil() {
        setting = new Setting(FileUtil.touch(settingFilePath), CharsetUtil.CHARSET_UTF_8, false);
    }

    public void setProps(String key, String value) {
        setting.put(key, value);
    }

    public String getProps(String key) {
        return setting.get(key);
    }

    /**
     * save to disk
     */
    public void save() {
        setting.store(settingFilePath);
    }
}
