package com.luoboduner.moo.info.ui;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.ui.component.TopMenuBar;
import com.luoboduner.moo.info.ui.form.NetworkForm;
import com.luoboduner.moo.info.ui.form.OverviewForm;
import com.luoboduner.moo.info.util.SystemUtil;
import com.luoboduner.moo.info.util.UIUtil;
import com.luoboduner.moo.info.util.UpgradeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The init Class
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/09.
 */
public class Init {

    private static final Log logger = LogFactory.get();

    /**
     * font size inti KEY
     */
    private static final String FONT_SIZE_INIT_PROP = "fontSizeInit";

    /**
     * set font for global
     */
    public static void initGlobalFont() {
        if (StringUtils.isEmpty(App.config.getProps(FONT_SIZE_INIT_PROP))) {
            // Adjust the font size according to the DPI
            // Gets the resolution of the screen dpi
            // dell 1920*1080/24 inch =96
            // Xiaomi air 1920*1080/13.3 inch =144
            // Xiaomi air 1366*768/13.3inch =96
            int fontSize = 12;

            // Initialize high-resolution screen font sizes such as Macs
            if (SystemUtil.isMacOs()) {
                fontSize = 15;
            } else {
                fontSize = (int) (UIUtil.getScreenScale() * fontSize);
            }
            App.config.setFontSize(fontSize);

            App.config.setProps(FONT_SIZE_INIT_PROP, "true");
            App.config.save();

            TopMenuBar.getInstance().initFontSizeMenu();
        }

        Font font = new Font(App.config.getFont(), Font.PLAIN, App.config.getFontSize());
        FontUIResource fontRes = new FontUIResource(font);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }

    }

    /**
     * Other initialization
     */
    public static void initOthers() {

    }

    /**
     * init look and feel
     */
    public static void initTheme() {
        if (SystemUtil.isMacM1() || SystemUtil.isLinuxOs()) {
            try {
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
                logger.warn("FlatDarculaLaf theme set.");
            } catch (Exception e) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e2) {
                    logger.error(ExceptionUtils.getStackTrace(e2));
                }
                logger.error(ExceptionUtils.getStackTrace(e));
            }
            return;
        }

        if (App.config.isUnifiedBackground()) {
            UIManager.put("TitlePane.unifiedBackground", true);
        }

        try {
            switch (App.config.getTheme()) {
                case "System Default":
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
                case "Flat Light":
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    FlatLightLaf.install();
                    break;
                case "Flat IntelliJ":
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatIntelliJLaf");
                    break;
                case "Flat Dark":
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
                    break;
                case "Darcula":
                case "Darcula(Recommended)":
                case "Flat Darcula(Recommended)":
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");

                    UIManager.put("PopupMenu.background", UIManager.getColor("Panel.background"));

/**
 If you don't like/want it, you can disable it with:
 UIManager.put( "TitlePane.useWindowDecorations", false );

 It is also possible to disable only the embedded menu bar (and keep the dark title pane) with:
 UIManager.put( "TitlePane.menuBarEmbedded", false );

 It is also possible to disable this on command line with following VM options:
 -Dflatlaf.useWindowDecorations=false
 -Dflatlaf.menuBarEmbedded=false

 If you have following code in your app, you can remove it (no longer necessary):
 // enable window decorations
 JFrame.setDefaultLookAndFeelDecorated( true );
 JDialog.setDefaultLookAndFeelDecorated( true );
 **/
                    break;
                case "Dark purple":
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    IntelliJTheme.setup(App.class.getResourceAsStream(
                            "/theme/DarkPurple.theme.json"));
                    break;
                case "IntelliJ Cyan":
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    IntelliJTheme.setup(App.class.getResourceAsStream(
                            "/theme/Cyan.theme.json"));
                    break;
                case "IntelliJ Light":
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    IntelliJTheme.setup(App.class.getResourceAsStream(
                            "/theme/Light.theme.json"));
                    break;

                default:
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * init all tab
     */
    public static void initAllTab() {

        ThreadUtil.execute(OverviewForm::init);
        ThreadUtil.execute(NetworkForm::init);

        // Check the new version
        if (App.config.isAutoCheckUpdate()) {
            ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(1);
            threadPoolExecutor.scheduleAtFixedRate(() -> UpgradeUtil.checkUpdate(true), 0, 24, TimeUnit.HOURS);
        }
    }

    public static void showMainFrame() {
        App.mainFrame.setVisible(true);
        if (App.mainFrame.getExtendedState() == Frame.ICONIFIED) {
            App.mainFrame.setExtendedState(Frame.NORMAL);
        } else if (App.mainFrame.getExtendedState() == 7) {
            App.mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        App.mainFrame.requestFocus();
    }

    public static void shutdown() {
        App.mainFrame.dispose();
        System.exit(0);
    }
}
