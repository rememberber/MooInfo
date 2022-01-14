package com.luoboduner.moo.info;

import com.formdev.flatlaf.extras.FlatDesktop;
import com.formdev.flatlaf.util.SystemInfo;
import com.luoboduner.moo.info.ui.Init;
import com.luoboduner.moo.info.ui.dialog.AboutDialog;
import com.luoboduner.moo.info.ui.dialog.SettingDialog;
import com.luoboduner.moo.info.ui.form.LoadingForm;
import com.luoboduner.moo.info.ui.form.MainWindow;
import com.luoboduner.moo.info.ui.frame.MainFrame;
import com.luoboduner.moo.info.util.ConfigUtil;
import com.luoboduner.moo.info.util.UIUtil;
import com.luoboduner.moo.info.util.UpgradeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Main Enter!
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/07.
 */
@Slf4j
public class App {

    public static ConfigUtil config = ConfigUtil.getInstance();

    public static MainFrame mainFrame;

    public static oshi.SystemInfo si;

    public static void main(String[] args) {

        if (SystemInfo.isMacOS) {
//            java -Xdock:name="MooInfo" -Xdock:icon=MooInfo.jpg ... (whatever else you normally specify here)
//            java -Xms64m -Xmx256m -Dapple.awt.application.name="MooInfo" -Dcom.apple.mrj.application.apple.menu.about.name="MooInfo" -cp "./lib/*" com.luoboduner.moo.info.App
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "MooInfo");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MooInfo");
            if (UIUtil.isDarkLaf()) {
                System.setProperty("apple.awt.application.appearance", "system");
            }

            FlatDesktop.setAboutHandler(() -> {
                try {
                    AboutDialog dialog = new AboutDialog();

                    dialog.pack();
                    dialog.setVisible(true);
                } catch (Exception e2) {
                    log.error(ExceptionUtils.getStackTrace(e2));
                }
            });
            FlatDesktop.setPreferencesHandler(() -> {
                try {
                    SettingDialog dialog = new SettingDialog();

                    dialog.pack();
                    dialog.setVisible(true);
                } catch (Exception e2) {
                    log.error(ExceptionUtils.getStackTrace(e2));
                }
            });
            FlatDesktop.setQuitHandler(FlatDesktop.QuitResponse::performQuit);

        }

        Init.initTheme();
        mainFrame = new MainFrame();
        mainFrame.init();
        JPanel loadingPanel = new LoadingForm().getLoadingPanel();
        mainFrame.add(loadingPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (config.isDefaultMaxWindow() || screenSize.getWidth() <= 1366) {
            // The window is automatically maximized at low resolution
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        UpgradeUtil.smoothUpgrade();

        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        si = new oshi.SystemInfo();

        Init.initGlobalFont();
        mainFrame.setContentPane(MainWindow.getInstance().getMainPanel());
        MainWindow.getInstance().init();
        Init.initAllTab();
        Init.initOthers();
        mainFrame.remove(loadingPanel);
    }
}
