package com.luoboduner.moo.info.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Helpers for running code on the Swing Event Dispatch Thread (EDT).
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 */
@Slf4j
public final class EdtUtil {

    private EdtUtil() {
    }

    /**
     * Run on the EDT. If already on the EDT, runs immediately; otherwise schedules via invokeLater.
     */
    public static void run(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /**
     * Always schedule on the EDT (never runs inline even if already on EDT).
     */
    public static void runLater(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    /**
     * Run on the EDT and wait. Prefer {@link #run} / {@link #runLater} when possible.
     */
    public static void runAndWait(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (InvocationTargetException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
