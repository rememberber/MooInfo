package com.luoboduner.moo.info.ui.form;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.ui.Style;
import com.luoboduner.moo.info.ui.UiConsts;
import lombok.Getter;
import oshi.hardware.CentralProcessor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * CpuForm
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/16.
 */
@Getter
public class CpuForm {
    private JPanel mainPanel;
    private JProgressBar scuProgressBar;
    private JPanel pcuPanel;
    private JPanel scuPanel;
    private JPanel pcuProgressBarPanel;
    private JLabel scuTitleLabel;
    private JLabel pcuTitleLabel;
    private JTextPane cpuInfoTextPane;

    private static final Log logger = LogFactory.get();

    private static CpuForm cpuForm;

    private static long[] prevTicks;

    public static CpuForm getInstance() {
        if (cpuForm == null) {
            cpuForm = new CpuForm();
        }
        return cpuForm;
    }

    public static void init() {
        cpuForm = getInstance();

        initUi();
        initCpuInfo();

        Timer timer = new Timer(UiConsts.REFRESH_FAST, e -> {
            initInfo();
        });
        timer.start();
    }

    private static void initUi() {
        CpuForm cpuForm = getInstance();

        Style.emphaticFont(cpuForm.getScuTitleLabel());
        Style.emphaticFont(cpuForm.getPcuTitleLabel());

        Dimension d = new Dimension(-1, 100);
        cpuForm.getScuProgressBar().setMinimumSize(d);

        CentralProcessor cpu = App.si.getHardware().getProcessor();
        int logicalProcessorCount = cpu.getLogicalProcessorCount();

        JPanel pcuProgressBarPanel = cpuForm.getPcuProgressBarPanel();
        pcuProgressBarPanel.setLayout(new GridLayoutManager(logicalProcessorCount, 2, new Insets(0, 0, 0, 0), -1, -1));
        for (int i = 0; i < logicalProcessorCount; i++) {
            JLabel label = new JLabel();
            label.setText("CPU" + i);
            pcuProgressBarPanel.add(label, new GridConstraints(i, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            JProgressBar progressBar = new JProgressBar();
            pcuProgressBarPanel.add(progressBar, new GridConstraints(i, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        }

    }

    private static void initCpuInfo() {
        CpuForm cpuForm = getInstance();
        JTextPane cpuInfoTextPane = cpuForm.getCpuInfoTextPane();

        StringBuilder sb = new StringBuilder();
        sb.append(App.si.getHardware().getProcessor());
        cpuInfoTextPane.setText(sb.toString());
    }

    private static void initInfo() {
        CentralProcessor processor = App.si.getHardware().getProcessor();
        DecimalFormat format = new DecimalFormat("#.00");

        if (prevTicks == null) {
            prevTicks = processor.getSystemCpuLoadTicks();
        }

        long[] ticks = processor.getSystemCpuLoadTicks();
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long totalCpu = Math.max(user + nice + cSys + idle + ioWait + irq + softIrq + steal, 0);

        prevTicks = ticks;

        double free = Double.parseDouble(format.format(idle <= 0 ? 0 : (100d * idle / totalCpu)));

        double cpuUsage = Double.parseDouble(format.format((100 - free)));

        CpuForm cpuForm = getInstance();
        cpuForm.getScuProgressBar().setMaximum(100);
        int cpuUsagePercent = (int) cpuUsage;
        cpuForm.getScuProgressBar().setValue(cpuUsagePercent);
        cpuForm.getScuProgressBar().setStringPainted(true);
        cpuForm.getScuProgressBar().setString(cpuUsage + "%");

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
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setContinuousLayout(true);
        splitPane1.setDividerLocation(300);
        splitPane1.setDividerSize(14);
        mainPanel.add(splitPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 0), -1, -1));
        splitPane1.setLeftComponent(panel1);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane1.setViewportView(panel2);
        scuPanel = new JPanel();
        scuPanel.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel2.add(scuPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scuTitleLabel = new JLabel();
        scuTitleLabel.setText("System CPU Usage");
        scuPanel.add(scuTitleLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scuProgressBar = new JProgressBar();
        scuPanel.add(scuProgressBar, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pcuPanel = new JPanel();
        pcuPanel.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel2.add(pcuPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pcuTitleLabel = new JLabel();
        pcuTitleLabel.setText("Processor CPU Usage");
        pcuPanel.add(pcuTitleLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pcuProgressBarPanel = new JPanel();
        pcuProgressBarPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        pcuPanel.add(pcuProgressBarPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(10, 0, 10, 10), -1, -1));
        splitPane1.setRightComponent(panel3);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cpuInfoTextPane = new JTextPane();
        cpuInfoTextPane.setEditable(false);
        panel4.add(cpuInfoTextPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
