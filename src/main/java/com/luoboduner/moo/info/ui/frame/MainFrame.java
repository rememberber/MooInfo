package com.luoboduner.moo.info.ui.frame;

import cn.hutool.core.thread.ThreadUtil;
import com.luoboduner.moo.info.ui.UiConsts;
import com.luoboduner.moo.info.ui.component.TopMenuBar;
import com.luoboduner.moo.info.ui.listener.FrameListener;
import com.luoboduner.moo.info.util.ComponentUtil;
import com.luoboduner.moo.info.util.FrameUtil;

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
        FrameUtil.setFrameIcon(this);
        TopMenuBar topMenuBar = TopMenuBar.getInstance();
        topMenuBar.init();
        setJMenuBar(topMenuBar);
        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.6, 0.66);
    }

    /**
     * add event listeners
     */
    public void addListeners() {
        ThreadUtil.execute(FrameListener::addListeners);
    }
}
