package com.luoboduner.moo.info.ui.frame;

import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.util.SystemInfo;
import com.luoboduner.moo.info.ui.UiConsts;
import com.luoboduner.moo.info.ui.component.TopMenuBar;
import com.luoboduner.moo.info.ui.listener.FrameListener;
import com.luoboduner.moo.info.util.ComponentUtil;
import com.luoboduner.moo.info.util.FrameUtil;
import com.luoboduner.moo.info.util.SystemUtil;

import javax.swing.*;
import java.net.URL;

/**
 * Main Frame
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/08.
 */
public class MainFrame extends JFrame {

    public void init() {
        this.setName(UiConsts.APP_NAME);
        this.setTitle(UiConsts.APP_NAME);
        // FlatLaf extras is modular; resolve SVG via app class, not FlatSVGUtils.class
        URL iconUrl = MainFrame.class.getResource("/icons/MooInfo.svg");
        if (iconUrl != null) {
            setIconImages(FlatSVGUtils.createWindowIconImages(iconUrl));
        } else {
            FrameUtil.setFrameIcon(this);
        }
        TopMenuBar topMenuBar = TopMenuBar.getInstance();
        topMenuBar.init();
        setJMenuBar(topMenuBar);
        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.6, 0.8);

        if (SystemUtil.isMacOs() && SystemInfo.isMacFullWindowContentSupported) {
            this.getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
            this.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
            this.getRootPane().putClientProperty("apple.awt.fullscreenable", true);
            this.getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
        }

        FrameListener.addListeners();
    }

}
