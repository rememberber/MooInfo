package com.luoboduner.moo.info.ui.form;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.luoboduner.moo.info.App;
import lombok.Getter;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.UsbDevice;

import javax.swing.*;
import java.awt.*;

/**
 * UsbForm
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/12.
 */
@Getter
public class UsbForm {

    private static final Log logger = LogFactory.get();

    private static UsbForm usbForm;
    private JPanel mainPanel;
    private JTextPane infoPane;

    public static UsbForm getInstance() {
        if (usbForm == null) {
            usbForm = new UsbForm();
        }
        return usbForm;
    }

    public static void init() {
        usbForm = getInstance();

        initUi();
        initInfo();
    }

    private static void initUi() {
    }

    private static void initInfo() {
        JTextPane infoPane = getInstance().getInfoPane();
        infoPane.setText(getUsbString(App.si.getHardware()));
    }

    /**
     * Codes are copied from oshi and have some modifications.
     *
     * @param hal
     * @return
     */
    private static String getUsbString(HardwareAbstractionLayer hal) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (UsbDevice usbDevice : hal.getUsbDevices(true)) {
            if (first) {
                first = false;
            } else {
                sb.append('\n');
            }
            sb.append(usbDevice);
        }
        return sb.toString();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(20, 20, 20, 20), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        infoPane = new JTextPane();
        infoPane.setEditable(false);
        scrollPane1.setViewportView(infoPane);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}