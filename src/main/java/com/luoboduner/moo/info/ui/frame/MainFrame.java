package com.luoboduner.moo.info.ui.frame;

import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.luoboduner.moo.info.ui.UiConsts;
import com.luoboduner.moo.info.ui.component.TopMenuBar;
import com.luoboduner.moo.info.ui.listener.FrameListener;
import com.luoboduner.moo.info.util.ComponentUtil;

import javax.swing.*;

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
//        FrameUtil.setFrameIcon(this);
        setIconImages(FlatSVGUtils.createWindowIconImages("/icons/MooInfo.svg"));
        TopMenuBar topMenuBar = TopMenuBar.getInstance();
        topMenuBar.init();
        setJMenuBar(topMenuBar);
        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.6, 0.8);

        FrameListener.addListeners();
    }

}
