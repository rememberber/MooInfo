package com.luoboduner.moo.info.ui;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.ui.component.TopMenuBar;
import com.luoboduner.moo.info.ui.form.*;
import com.luoboduner.moo.info.util.EdtUtil;
import com.luoboduner.moo.info.util.SystemUtil;
import com.luoboduner.moo.info.util.UIUtil;
import com.luoboduner.moo.info.util.UpgradeUtil;
import org.apache.commons.lang3.StringUtils;

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
                fontSize = 13;
            } else {
                fontSize = 12;
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

        try {
            switch (App.config.getTheme()) {
                case "System Default":
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
                case "Flat Light":
                    FlatLightLaf.setup();
                    break;
                case "Flat IntelliJ":
                    FlatIntelliJLaf.setup();
                    break;
                case "Flat Dark":
                    FlatDarkLaf.setup();
                    break;
                case "Dark purple":
                    FlatDarkPurpleIJTheme.setup();
                    break;
                case "IntelliJ Cyan":
                    FlatCyanLightIJTheme.setup();
                    break;
                case "IntelliJ Light":
                    FlatLightFlatIJTheme.setup();
                    break;
                case "Xcode-Dark":
                    FlatXcodeDarkIJTheme.setup();
                    break;
                case "Vuesion":
                    FlatVuesionIJTheme.setup();
                    break;
                case "Flat macOS Light":
                    FlatMacLightLaf.setup();
                    break;
                case "Flat macOS Dark":
                    FlatMacDarkLaf.setup();
                    break;
                default:
                    FlatDarculaLaf.setup();
            }

            if (UIUtil.isDarkLaf()) {
//                FlatSVGIcon.ColorFilter.getInstance().setMapper(color -> color.brighter().brighter());
            } else {
                FlatSVGIcon.ColorFilter.getInstance().setMapper(color -> color.darker().darker());
//                SwingUtilities.windowForComponent(App.mainFrame).repaint();
            }

            if (App.config.isUnifiedBackground()) {
                UIManager.put("TitlePane.unifiedBackground", true);
            }

            // top menubar background
            UIManager.put("PopupMenu.background", UIManager.getColor("Panel.background"));
            // arrow type
            UIManager.put("Component.arrowType", "chevron");
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * init all tab
     */
    public static void initAllTab() {
        // Form UI must be touched on the EDT; schedule each init asynchronously so
        // one slow tab does not block others from being queued.
        scheduleFormInit(OverviewForm::init);
        scheduleFormInit(DetailForm::init);
        scheduleFormInit(MemoryForm::init);
        scheduleFormInit(CpuForm::init);
        scheduleFormInit(NetworkForm::init);
        scheduleFormInit(UsbForm::init);
        scheduleFormInit(VariablesForm::init);
        scheduleFormInit(ProcessesForm::init);
        scheduleFormInit(DiskForm::init);
        scheduleFormInit(PowerSourceForm::init);

        // Check for updates shortly after startup, then hourly (aligned with MooTool)
        if (App.config.isAutoCheckUpdate()) {
            ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(1);
            threadPoolExecutor.scheduleAtFixedRate(() -> UpgradeUtil.checkUpdate(true), 3, 3600, TimeUnit.SECONDS);
        }
    }

    private static void scheduleFormInit(Runnable init) {
        ThreadUtil.execute(() -> EdtUtil.run(init));
    }

    public static void showMainFrame() {
        EdtUtil.run(() -> {
            App.mainFrame.setVisible(true);
            if (App.mainFrame.getExtendedState() == Frame.ICONIFIED) {
                App.mainFrame.setExtendedState(Frame.NORMAL);
            } else if (App.mainFrame.getExtendedState() == 7) {
                App.mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
            }
            App.mainFrame.requestFocus();
        });
    }

    public static void shutdown() {
        App.mainFrame.dispose();
        System.exit(0);
    }
}
