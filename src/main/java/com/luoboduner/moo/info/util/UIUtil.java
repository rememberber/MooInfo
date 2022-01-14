package com.luoboduner.moo.info.util;

import com.luoboduner.moo.info.App;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

/**
 * UI custom tools
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/10.
 */
@Slf4j
public class UIUtil {

    /**
     * Get screen specifications
     * <p>
     * author by darcula@com.bulenkov
     * see https://github.com/bulenkov/Darcula
     *
     * @return
     */
    public static float getScreenScale() {
        int dpi = 96;

        try {
            dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        } catch (HeadlessException var2) {
        }

        float scale = 1.0F;
        if (dpi < 120) {
            scale = 1.0F;
        } else if (dpi < 144) {
            scale = 1.25F;
        } else if (dpi < 168) {
            scale = 1.5F;
        } else if (dpi < 192) {
            scale = 1.75F;
        } else {
            scale = 2.0F;
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        log.info("screen dpi:{},width:{},height:{}", dpi, screenSize.getWidth(), screenSize.getHeight());

        return scale;
    }

    /**
     * the theme is dark or not
     *
     * @return
     */
    public static boolean isDarkLaf() {
        return "Darcula".equals(App.config.getTheme())
                || "Darcula(Recommended)".equals(App.config.getTheme())
                || "Flat Dark".equals(App.config.getTheme())
                || "Flat Darcula".equals(App.config.getTheme())
                || "Dark purple".equals(App.config.getTheme())
                || "Flat Darcula(Recommended)".equals(App.config.getTheme());
    }
}
