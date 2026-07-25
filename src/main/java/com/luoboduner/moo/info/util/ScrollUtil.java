package com.luoboduner.moo.info.util;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * some functions about scroll
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/23.
 */
public class ScrollUtil {

    public static void smoothPane(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(14);
        scrollPane.getVerticalScrollBar().setDoubleBuffered(true);
        scrollPane.getHorizontalScrollBar().setDoubleBuffered(true);
    }

    /**
     * setText moves caret to the end and scrolls nested panes down; keep content at top.
     */
    public static void setTextAtTop(JTextComponent textComponent, String text) {
        textComponent.setText(text);
        textComponent.setCaretPosition(0);
    }

    public static void scrollToTop(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMinimum());
        SwingUtilities.invokeLater(() -> verticalScrollBar.setValue(verticalScrollBar.getMinimum()));
    }
}
