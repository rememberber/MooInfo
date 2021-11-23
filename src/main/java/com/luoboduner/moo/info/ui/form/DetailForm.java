package com.luoboduner.moo.info.ui.form;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.ui.Style;
import com.luoboduner.moo.info.util.ScrollUtil;
import lombok.Getter;
import oshi.hardware.Baseboard;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * DetailForm
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/12.
 */
@Getter
public class DetailForm {
    private JPanel mainPanel;
    private JTextPane osTextPane;
    private JLabel osLabel;
    private JTextPane computerTextPane;
    private JLabel computerLabel;
    private JLabel baseBoardLabel;
    private JTextPane baseBoardTextPane;
    private JTextPane cpuTextPane;
    private JLabel cpuLabel;
    private JLabel memoryLabel;
    private JTextPane memoryTextPane;
    private JTextPane storageTextPane;
    private JLabel storageLabel;
    private JTextPane graphicsCardTextPane;
    private JLabel graphicsCardLabel;
    private JLabel displayLabel;
    private JTextPane displayTextPane;
    private JTextPane soundCardTextPane;
    private JLabel soundCardLabel;
    private JTextPane networkTextPane;
    private JLabel networkLabel;
    private JTextPane powerSourceTextPane;
    private JLabel powerSourceLabel;
    private JLabel sensorsLabel;
    private JTextPane sensorsTextPane;
    private JScrollPane scrollPane;

    private static final Log logger = LogFactory.get();

    private static DetailForm detailForm;

    public static DetailForm getInstance() {
        if (detailForm == null) {
            detailForm = new DetailForm();
        }
        return detailForm;
    }

    public static void init() {
        detailForm = getInstance();

        initUi();
        initInfo();
    }

    private static void initUi() {
        DetailForm detailForm = getInstance();

        ScrollUtil.smoothPane(detailForm.scrollPane);

        Style.emphaticTitleFont(detailForm.getOsLabel());
        Style.emphaticTitleFont(detailForm.getComputerLabel());
        Style.emphaticTitleFont(detailForm.getBaseBoardLabel());
        Style.emphaticTitleFont(detailForm.getCpuLabel());
        Style.emphaticTitleFont(detailForm.getMemoryLabel());
        Style.emphaticTitleFont(detailForm.getStorageLabel());
        Style.emphaticTitleFont(detailForm.getGraphicsCardLabel());
        Style.emphaticTitleFont(detailForm.getDisplayLabel());
        Style.emphaticTitleFont(detailForm.getSoundCardLabel());
        Style.emphaticTitleFont(detailForm.getNetworkLabel());
        Style.emphaticTitleFont(detailForm.getPowerSourceLabel());
        Style.emphaticTitleFont(detailForm.getSensorsLabel());

        detailForm.getOsTextPane().setContentType("text/html; charset=utf-8");
        detailForm.getComputerTextPane().setContentType("text/html; charset=utf-8");
        detailForm.getBaseBoardTextPane().setContentType("text/html; charset=utf-8");
        detailForm.getCpuTextPane().setContentType("text/html; charset=utf-8");
        detailForm.getMemoryTextPane().setContentType("text/html; charset=utf-8");
        detailForm.getStorageTextPane().setContentType("text/html; charset=utf-8");
        detailForm.getGraphicsCardTextPane().setContentType("text/html; charset=utf-8");
        detailForm.getDisplayTextPane().setContentType("text/html; charset=utf-8");
        detailForm.getSoundCardTextPane().setContentType("text/html; charset=utf-8");
        detailForm.getNetworkTextPane().setContentType("text/html; charset=utf-8");
        detailForm.getPowerSourceTextPane().setContentType("text/html; charset=utf-8");
        detailForm.getSensorsTextPane().setContentType("text/html; charset=utf-8");
    }

    private static void initInfo() {
        DetailForm detailForm = getInstance();

        HardwareAbstractionLayer hardware = App.si.getHardware();
        ComputerSystem computerSystem = hardware.getComputerSystem();

        detailForm.getOsTextPane().setText(getOsInfo());
        detailForm.getComputerTextPane().setText(getComputerInfo());
        detailForm.getPowerSourceTextPane().setText(PowerSourceForm.getPowerInfoText(hardware.getPowerSources()));
    }

    private static String getOsInfo() {
        StringBuilder builder = new StringBuilder();
        OperatingSystem operatingSystem = App.si.getOperatingSystem();

        builder.append("<b>Manufacturer: </b>").append(operatingSystem.getManufacturer());
        builder.append("<br/><b>Family: </b>").append(operatingSystem.getFamily());
        builder.append("<br/><b>Version: </b>").append(operatingSystem.getVersionInfo());
        builder.append("<br/><b>Bitness: </b>").append(operatingSystem.getBitness());
        builder.append("<br/><b>Max File Descriptors: </b>").append(operatingSystem.getFileSystem().getMaxFileDescriptors());
        builder.append("<br/><b>Open File Descriptors: </b>").append(operatingSystem.getFileSystem().getOpenFileDescriptors());
        builder.append("<br/><b>Thread Count: </b>").append(operatingSystem.getThreadCount());
        builder.append("<br/><b>Process Count: </b>").append(operatingSystem.getProcessCount());
        builder.append("<br/><b>System Boot Time: </b>").append(operatingSystem.getSystemBootTime());
        builder.append("<br/><b>System Uptime: </b>").append(operatingSystem.getSystemUptime());

        return builder.toString();
    }

    private static String getComputerInfo() {
        StringBuilder builder = new StringBuilder();
        ComputerSystem computerSystem = App.si.getHardware().getComputerSystem();

        builder.append("<b>Manufacturer: </b>").append(computerSystem.getManufacturer());
        builder.append("<br/><b>Model: </b>").append(computerSystem.getModel());
        builder.append("<br/><b>Serial Number: </b>").append(computerSystem.getSerialNumber());
        builder.append("<br/><b>Hardware UUID: </b>").append(computerSystem.getHardwareUUID());
        builder.append("<br/><b>Firmware Manufacturer: </b>").append(computerSystem.getFirmware().getManufacturer());
        builder.append("<br/><b>Firmware Name: </b>").append(computerSystem.getFirmware().getName());
        builder.append("<br/><b>Firmware Description: </b>").append(computerSystem.getFirmware().getDescription());
        builder.append("<br/><b>Firmware Version: </b>").append(computerSystem.getFirmware().getVersion());
        builder.append("<br/><b>Firmware Release Date: </b>").append(computerSystem.getFirmware().getReleaseDate());

        return builder.toString();
    }

    private static String getBaseBoardInfo() {
        StringBuilder builder = new StringBuilder();
        Baseboard baseboard = App.si.getHardware().getComputerSystem().getBaseboard();


        return builder.toString();
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
        scrollPane = new JScrollPane();
        mainPanel.add(scrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(13, 1, new Insets(10, 10, 10, 10), -1, -1));
        scrollPane.setViewportView(panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        osLabel = new JLabel();
        osLabel.setText("Operating System");
        panel2.add(osLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        osTextPane = new JTextPane();
        panel2.add(osTextPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        computerLabel = new JLabel();
        computerLabel.setText("Computer");
        panel3.add(computerLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        computerTextPane = new JTextPane();
        panel3.add(computerTextPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        baseBoardLabel = new JLabel();
        baseBoardLabel.setText("Base Board");
        panel4.add(baseBoardLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        baseBoardTextPane = new JTextPane();
        panel4.add(baseBoardTextPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cpuLabel = new JLabel();
        cpuLabel.setText("CPU");
        panel5.add(cpuLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cpuTextPane = new JTextPane();
        panel5.add(cpuTextPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel6, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        memoryLabel = new JLabel();
        memoryLabel.setText("Memory");
        panel6.add(memoryLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        memoryTextPane = new JTextPane();
        panel6.add(memoryTextPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel7, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        storageLabel = new JLabel();
        storageLabel.setText("Storage");
        panel7.add(storageLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        storageTextPane = new JTextPane();
        panel7.add(storageTextPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel8, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        graphicsCardLabel = new JLabel();
        graphicsCardLabel.setText("Graphics Card");
        panel8.add(graphicsCardLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        graphicsCardTextPane = new JTextPane();
        panel8.add(graphicsCardTextPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel9, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        displayLabel = new JLabel();
        displayLabel.setText("Display");
        panel9.add(displayLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        displayTextPane = new JTextPane();
        panel9.add(displayTextPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel10, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        soundCardLabel = new JLabel();
        soundCardLabel.setText("Sound Card");
        panel10.add(soundCardLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        soundCardTextPane = new JTextPane();
        panel10.add(soundCardTextPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel11, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        networkLabel = new JLabel();
        networkLabel.setText("Network");
        panel11.add(networkLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        networkTextPane = new JTextPane();
        panel11.add(networkTextPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel12, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        powerSourceLabel = new JLabel();
        powerSourceLabel.setText("Power Source");
        panel12.add(powerSourceLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        powerSourceTextPane = new JTextPane();
        panel12.add(powerSourceTextPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel13, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        sensorsLabel = new JLabel();
        sensorsLabel.setText("Sensors");
        panel13.add(sensorsLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sensorsTextPane = new JTextPane();
        panel13.add(sensorsTextPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
