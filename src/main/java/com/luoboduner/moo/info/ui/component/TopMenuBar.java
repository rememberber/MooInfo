package com.luoboduner.moo.info.ui.component;

import cn.hutool.core.thread.ThreadUtil;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.ui.Init;
import com.luoboduner.moo.info.ui.dialog.AboutDialog;
import com.luoboduner.moo.info.ui.dialog.SettingDialog;
import com.luoboduner.moo.info.ui.dialog.SystemEnvResultDialog;
import com.luoboduner.moo.info.ui.dialog.SystemInfoTestDialog;
import com.luoboduner.moo.info.ui.form.MainWindow;
import com.luoboduner.moo.info.util.SystemUtil;
import com.luoboduner.moo.info.util.UpgradeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * The top menu bar
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/10.
 */
@Slf4j
public class TopMenuBar extends JMenuBar {

    private static TopMenuBar menuBar;

    private static JMenu themeMenu;

    private static JMenu fontFamilyMenu;

    private static JMenu fontSizeMenu;

    private static int initialThemeItemCount = -1;

    private static int initialFontFamilyItemCount = -1;

    private static int initialFontSizeItemCount = -1;

    private static String[] themeNames = {
            "System Default",
            "Flat Light",
            "Flat IntelliJ",
            "Flat Dark",
            "Flat Darcula(Recommended)",
            "Dark purple",
            "IntelliJ Cyan",
            "IntelliJ Light"};

    private static String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

    private static String[] fontSizes = {
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26"};

    private TopMenuBar() {
    }

    public static TopMenuBar getInstance() {
        if (menuBar == null) {
            menuBar = new TopMenuBar();
        }
        return menuBar;
    }

    public void init() {
        TopMenuBar topMenuBar = getInstance();
        // ---------App
        JMenu appMenu = new JMenu();
        appMenu.setText("App");
        // Setting
        JMenuItem settingMenuItem = new JMenuItem();
        settingMenuItem.setText("Settings");
        settingMenuItem.addActionListener(e -> settingActionPerformed());
        appMenu.add(settingMenuItem);

        // Show logs
        JMenuItem logMenuItem = new JMenuItem();
        logMenuItem.setText("Show logs");
        logMenuItem.addActionListener(e -> logActionPerformed());
        appMenu.add(logMenuItem);

        // System Info Test
        JMenuItem syInfoTestMenuItem = new JMenuItem();
        syInfoTestMenuItem.setText("System Info Test");
        syInfoTestMenuItem.addActionListener(e -> sysInfoTestActionPerformed());
        appMenu.add(syInfoTestMenuItem);

        // System environment variables
        JMenuItem sysEnvMenuItem = new JMenuItem();
        sysEnvMenuItem.setText("System environment variables");
        sysEnvMenuItem.addActionListener(e -> sysEnvActionPerformed());
        appMenu.add(sysEnvMenuItem);

        // Exit
        JMenuItem exitMenuItem = new JMenuItem();
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(e -> exitActionPerformed());
        appMenu.add(exitMenuItem);
        topMenuBar.add(appMenu);

        // ---------Appearance
        JMenu appearanceMenu = new JMenu();
        appearanceMenu.setText("Appearance");

        JCheckBoxMenuItem defaultMaxWindowitem = new JCheckBoxMenuItem("Maximize window by Default");
        defaultMaxWindowitem.setSelected(App.config.isDefaultMaxWindow());
        defaultMaxWindowitem.addActionListener(e -> {
            boolean selected = defaultMaxWindowitem.isSelected();
            if (selected) {
                App.mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                App.mainFrame.setExtendedState(JFrame.NORMAL);
            }
            App.config.setDefaultMaxWindow(selected);
            App.config.save();
        });
        appearanceMenu.add(defaultMaxWindowitem);

        JCheckBoxMenuItem unifiedBackgrounditem = new JCheckBoxMenuItem("Window color immersive");
        unifiedBackgrounditem.setSelected(App.config.isUnifiedBackground());
        unifiedBackgrounditem.addActionListener(e -> {
            boolean selected = unifiedBackgrounditem.isSelected();
            App.config.setUnifiedBackground(selected);
            App.config.save();
            UIManager.put("TitlePane.unifiedBackground", selected);
            FlatLaf.updateUI();
        });
        appearanceMenu.add(unifiedBackgrounditem);

        themeMenu = new JMenu();
        themeMenu.setText("Theme");

        initThemesMenu();

        appearanceMenu.add(themeMenu);

        fontFamilyMenu = new JMenu();
        fontFamilyMenu.setText("Font Family");
        initFontFamilyMenu();

        appearanceMenu.add(fontFamilyMenu);

        fontSizeMenu = new JMenu();
        fontSizeMenu.setText("Font Size");
        initFontSizeMenu();

        appearanceMenu.add(fontSizeMenu);

        topMenuBar.add(appearanceMenu);

        // ---------About
        JMenu aboutMenu = new JMenu();
        aboutMenu.setText("About");

        // Check for Updates
        JMenuItem checkForUpdatesItem = new JMenuItem();
        checkForUpdatesItem.setText("Check for Updates");
        checkForUpdatesItem.addActionListener(e -> checkForUpdatesActionPerformed());
        aboutMenu.add(checkForUpdatesItem);

        // About
        JMenuItem aboutMenuItem = new JMenuItem();
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(e -> aboutActionPerformed());
        aboutMenu.add(aboutMenuItem);

        topMenuBar.add(aboutMenu);
    }

    private void checkForUpdatesActionPerformed() {
        ThreadUtil.execute(() -> UpgradeUtil.checkUpdate(false));
    }

    public void initFontSizeMenu() {

        if (initialFontSizeItemCount < 0)
            initialFontSizeItemCount = fontSizeMenu.getItemCount();
        else {
            // remove old items
            for (int i = fontSizeMenu.getItemCount() - 1; i >= initialFontSizeItemCount; i--)
                fontSizeMenu.remove(i);
        }
        for (String fontSize : fontSizes) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(fontSize);
            item.setSelected(fontSize.equals(String.valueOf(App.config.getFontSize())));
            item.addActionListener(this::fontSizeChanged);
            fontSizeMenu.add(item);
        }
    }


    private void initFontFamilyMenu() {

        if (initialFontFamilyItemCount < 0)
            initialFontFamilyItemCount = fontFamilyMenu.getItemCount();
        else {
            // remove old items
            for (int i = fontFamilyMenu.getItemCount() - 1; i >= initialFontFamilyItemCount; i--)
                fontFamilyMenu.remove(i);
        }
        for (String font : fontNames) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(font);
            item.setSelected(font.equals(App.config.getFont()));
            item.addActionListener(this::fontFamilyChanged);
            fontFamilyMenu.add(item);
        }
    }

    private void initThemesMenu() {

        if (initialThemeItemCount < 0)
            initialThemeItemCount = themeMenu.getItemCount();
        else {
            // remove old items
            for (int i = themeMenu.getItemCount() - 1; i >= initialThemeItemCount; i--)
                themeMenu.remove(i);
        }
        for (String themeName : themeNames) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(themeName);
            item.setSelected(themeName.equals(App.config.getTheme()));
            item.addActionListener(this::themeChanged);
            themeMenu.add(item);
        }
    }

    private void fontSizeChanged(ActionEvent actionEvent) {
        try {
            String selectedFontSize = actionEvent.getActionCommand();

            FlatAnimatedLafChange.showSnapshot();

            App.config.setFontSize(Integer.parseInt(selectedFontSize));
            App.config.save();

            Init.initGlobalFont();
            SwingUtilities.updateComponentTreeUI(App.mainFrame);
            SwingUtilities.updateComponentTreeUI(MainWindow.getInstance().getTabbedPane());

//                FlatLaf.updateUI();

            FlatAnimatedLafChange.hideSnapshotWithAnimation();

            initFontSizeMenu();

        } catch (Exception e1) {
            JOptionPane.showMessageDialog(MainWindow.getInstance().getMainPanel(), "Save failed!\n\n" + e1.getMessage(), "Failed",
                    JOptionPane.ERROR_MESSAGE);
            log.error(ExceptionUtils.getStackTrace(e1));
        }
    }


    private void fontFamilyChanged(ActionEvent actionEvent) {
        try {
            String selectedFamily = actionEvent.getActionCommand();

            FlatAnimatedLafChange.showSnapshot();

            App.config.setFont(selectedFamily);
            App.config.save();

            Init.initGlobalFont();
            SwingUtilities.updateComponentTreeUI(App.mainFrame);
            SwingUtilities.updateComponentTreeUI(MainWindow.getInstance().getTabbedPane());

//                FlatLaf.updateUI();

            FlatAnimatedLafChange.hideSnapshotWithAnimation();

            initFontFamilyMenu();

        } catch (Exception e1) {
            JOptionPane.showMessageDialog(MainWindow.getInstance().getMainPanel(), "Save failed!\n\n" + e1.getMessage(), "Failed",
                    JOptionPane.ERROR_MESSAGE);
            log.error(ExceptionUtils.getStackTrace(e1));
        }
    }

    private void themeChanged(ActionEvent actionEvent) {
        try {
            String selectedThemeName = actionEvent.getActionCommand();

            FlatAnimatedLafChange.showSnapshot();

            App.config.setTheme(selectedThemeName);
            App.config.save();

            Init.initTheme();
            SwingUtilities.updateComponentTreeUI(App.mainFrame);
            SwingUtilities.updateComponentTreeUI(MainWindow.getInstance().getTabbedPane());

//                FlatLaf.updateUI();

            FlatAnimatedLafChange.hideSnapshotWithAnimation();

            initThemesMenu();

        } catch (Exception e1) {
            JOptionPane.showMessageDialog(MainWindow.getInstance().getMainPanel(), "Save failed!\n\n" + e1.getMessage(), "Failed",
                    JOptionPane.ERROR_MESSAGE);
            log.error(ExceptionUtils.getStackTrace(e1));
        }
    }

    private void aboutActionPerformed() {
        try {
            AboutDialog dialog = new AboutDialog();

            dialog.pack();
            dialog.setVisible(true);
        } catch (Exception e2) {
            log.error(ExceptionUtils.getStackTrace(e2));
        }
    }

    private void sysInfoTestActionPerformed() {
        try {
            SystemInfoTestDialog dialog = new SystemInfoTestDialog();
            dialog.pack();
            dialog.setVisible(true);
        } catch (Exception e2) {
            log.error("Show system info test dialog failed", e2);
        }
    }

    private void sysEnvActionPerformed() {
        try {
            SystemEnvResultDialog dialog = new SystemEnvResultDialog();

            dialog.appendTextArea("------------System.getenv---------------");
            Map<String, String> map = System.getenv();
            for (Map.Entry<String, String> envEntry : map.entrySet()) {
                dialog.appendTextArea(envEntry.getKey() + "=" + envEntry.getValue());
            }

            dialog.appendTextArea("------------System.getProperties---------------");
            Properties properties = System.getProperties();
            for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
                dialog.appendTextArea(objectObjectEntry.getKey() + "=" + objectObjectEntry.getValue());
            }

            dialog.pack();
            dialog.setVisible(true);
        } catch (Exception e2) {
            log.error("Show system environment variables failed", e2);
        }
    }

    private void logActionPerformed() {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(SystemUtil.LOG_DIR));
        } catch (Exception e2) {
            log.error("Show log failed", e2);
        }
    }

    private void exitActionPerformed() {
        Init.shutdown();
    }

    private void settingActionPerformed() {
        try {
            SettingDialog dialog = new SettingDialog();

            dialog.pack();
            dialog.setVisible(true);
        } catch (Exception e2) {
            log.error(ExceptionUtils.getStackTrace(e2));
        }
    }
}
