package com.luoboduner.moo.info.util;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import javax.swing.*;
import java.util.Date;

/**
 * <pre>
 * ConsoleUtil to print text into textarea
 * </pre>
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/12/15.
 */
@Slf4j
public class ConsoleUtil {

    private static final Log logger = LogFactory.get();

    public static void consoleWithLog(JTextArea textArea, String log) {
        textArea.append(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS") + " ");
        textArea.append(log + "\n");
        textArea.setCaretPosition(textArea.getText().length());
        logger.warn(log);
    }

    public static void consoleOnly(JTextArea textArea, String log) {
        textArea.append(log + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

}
