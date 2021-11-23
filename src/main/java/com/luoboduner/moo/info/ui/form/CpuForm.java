package com.luoboduner.moo.info.ui.form;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.ui.Style;
import lombok.Getter;
import oshi.hardware.CentralProcessor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private JPanel panel1;
    private JPanel pcfPanel;
    private JLabel pcfLabel;
    private JLabel usageLabel;
    private JLabel freqLabel;
    private JLabel InterruptsLabel;
    private JLabel contextSwitchesLabel;

    private static final Log logger = LogFactory.get();

    private static CpuForm cpuForm;

    private static long[] prevTicks;

    private static long[][] preProcessorTicks;

    private static List<JProgressBar> processorProgressBars;
    private static List<JTextField> processorTextFields;

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

        ScheduledExecutorService serviceStartPerSecond = Executors.newSingleThreadScheduledExecutor();
        serviceStartPerSecond.scheduleAtFixedRate(() -> {
            initPcuInfo();
            initPcfInfo();
            initIndicatorInfo();
        }, 0, 1, TimeUnit.SECONDS);

    }

    private static void initUi() {
        CpuForm cpuForm = getInstance();

        Style.emphaticTitleFont(cpuForm.getScuTitleLabel());
        Style.emphaticTitleFont(cpuForm.getPcuTitleLabel());
        Style.emphaticTitleFont(cpuForm.getPcfLabel());

        Style.emphaticIndicatorFont(cpuForm.getUsageLabel());
        Style.emphaticIndicatorFont(cpuForm.getFreqLabel());
        Style.emphaticIndicatorFont(cpuForm.getInterruptsLabel());
        Style.emphaticIndicatorFont(cpuForm.getContextSwitchesLabel());

        Dimension d = new Dimension(-1, 100);
        cpuForm.getScuProgressBar().setMinimumSize(d);

        CentralProcessor cpu = App.si.getHardware().getProcessor();
        int logicalProcessorCount = cpu.getLogicalProcessorCount();

        JPanel pcuProgressBarPanel = cpuForm.getPcuProgressBarPanel();
        pcuProgressBarPanel.setLayout(new GridLayoutManager(logicalProcessorCount, 2, new Insets(0, 0, 0, 0), -1, -1));

        JPanel pcfPanel = cpuForm.getPcfPanel();
        pcfPanel.setLayout(new GridLayoutManager(logicalProcessorCount, 2, new Insets(0, 0, 0, 0), -1, -1));

        processorProgressBars = new ArrayList<>();
        processorTextFields = new ArrayList<>();
        for (int i = 0; i < logicalProcessorCount; i++) {
            JLabel label = new JLabel();
            label.setText("CPU " + i);
            pcuProgressBarPanel.add(label, new GridConstraints(i, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            JProgressBar progressBar = new JProgressBar();
            pcuProgressBarPanel.add(progressBar, new GridConstraints(i, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            processorProgressBars.add(progressBar);

            JLabel labelPcf = new JLabel();
            labelPcf.setText("CPU " + i);
            pcfPanel.add(labelPcf, new GridConstraints(i, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            JTextField textField = new JTextField();
            textField.setEditable(false);
            textField.setForeground(progressBar.getForeground());
            pcfPanel.add(textField, new GridConstraints(i, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            processorTextFields.add(textField);
        }

    }

    private static void initCpuInfo() {
        CpuForm cpuForm = getInstance();
        JTextPane cpuInfoTextPane = cpuForm.getCpuInfoTextPane();
        String contentType = "text/html; charset=utf-8";
        cpuInfoTextPane.setContentType(contentType);
        cpuInfoTextPane.setText(getCpuInfo());
    }

    public static String getCpuInfo() {
        StringBuilder builder = new StringBuilder();
        CentralProcessor processor = App.si.getHardware().getProcessor();

        CentralProcessor.ProcessorIdentifier processorIdentifier = processor.getProcessorIdentifier();
        builder.append("<b>Name: </b>").append(processorIdentifier.getName());
        builder.append("<br/><b>Identifier: </b>").append(processorIdentifier.getIdentifier());
        builder.append("<br/><b>Micro Architecture: </b>").append(processorIdentifier.getMicroarchitecture());
        builder.append("<br/><b>Model: </b>").append(processorIdentifier.getModel());
        builder.append("<br/><b>Family: </b>").append(processorIdentifier.getFamily());
        builder.append("<br/><b>Processor ID: </b>").append(processorIdentifier.getProcessorID());
        builder.append("<br/><b>Vendor: </b>").append(processorIdentifier.getVendor());
        builder.append("<br/><b>Vendor Freq: </b>").append(new BigDecimal(processorIdentifier.getVendorFreq()).divide(new BigDecimal(1000000000), 2, RoundingMode.HALF_UP)).append(" GHz");
        builder.append("<br/><b>Stepping: </b>").append(processorIdentifier.getStepping());
        builder.append("<br/>");
        builder.append("<br/><b>Physical Package Count: </b>").append(processor.getPhysicalPackageCount());
        builder.append("<br/><b>Physical Processor Count: </b>").append(processor.getPhysicalProcessorCount());
        builder.append("<br/><b>Logical Processor Count: </b>").append(processor.getLogicalProcessorCount());
        builder.append("<br/><b>Max Freq: </b>").append(new BigDecimal(processor.getMaxFreq()).divide(new BigDecimal(1000000000), 2, RoundingMode.HALF_UP)).append(" GHz");

        return builder.toString();

    }

    private static void initPcuInfo() {
        CentralProcessor processor = App.si.getHardware().getProcessor();
        DecimalFormat format = new DecimalFormat("#.00");

        long[] ticks = processor.getSystemCpuLoadTicks();
        if (prevTicks == null) {
            prevTicks = ticks;
        }

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
        JProgressBar scuProgressBar = cpuForm.getScuProgressBar();
        scuProgressBar.setMaximum(100);
        int cpuUsagePercent = (int) cpuUsage;
        scuProgressBar.setValue(cpuUsagePercent);
        scuProgressBar.setStringPainted(true);
        String cpuUsageStr = cpuUsage + "%";
        scuProgressBar.setString(cpuUsageStr);
        cpuForm.getUsageLabel().setText(cpuUsageStr);

        long[][] processorTicks = processor.getProcessorCpuLoadTicks();
        if (preProcessorTicks == null) {
            preProcessorTicks = processorTicks;
        }

        for (int i = 0; i < processorTicks.length; i++) {
            long[] pTicks = processorTicks[i];
            long[] prePTicks = preProcessorTicks[i];

            long pUser = pTicks[CentralProcessor.TickType.USER.getIndex()] - prePTicks[CentralProcessor.TickType.USER.getIndex()];
            long pNice = pTicks[CentralProcessor.TickType.NICE.getIndex()] - prePTicks[CentralProcessor.TickType.NICE.getIndex()];
            long pCSys = pTicks[CentralProcessor.TickType.SYSTEM.getIndex()] - prePTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
            long pIoWait = pTicks[CentralProcessor.TickType.IOWAIT.getIndex()] - prePTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
            long pIdle = pTicks[CentralProcessor.TickType.IDLE.getIndex()] - prePTicks[CentralProcessor.TickType.IDLE.getIndex()];
            long pIrq = pTicks[CentralProcessor.TickType.IRQ.getIndex()] - prePTicks[CentralProcessor.TickType.IRQ.getIndex()];
            long pSoftIrq = pTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prePTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
            long pSteal = pTicks[CentralProcessor.TickType.STEAL.getIndex()] - prePTicks[CentralProcessor.TickType.STEAL.getIndex()];
            long pTotalCpu = Math.max(pUser + pNice + pCSys + pIdle + pIoWait + pIrq + pSoftIrq + pSteal, 0);

            double pFree = Double.parseDouble(format.format(pIdle <= 0 ? 0 : (100d * pIdle / pTotalCpu)));

            double pCpuUsage = Double.parseDouble(format.format((100 - pFree)));

            JProgressBar jProgressBar = processorProgressBars.get(i);
            jProgressBar.setMaximum(100);
            int pCpuUsagePercent = (int) pCpuUsage;
            jProgressBar.setValue(pCpuUsagePercent);
            jProgressBar.setStringPainted(true);
            jProgressBar.setString(pCpuUsage + "%");
            jProgressBar.setToolTipText(pCpuUsage + "%");

        }

        preProcessorTicks = processorTicks;

    }

    private static void initPcfInfo() {
        CpuForm cpuForm = getInstance();
        CentralProcessor processor = App.si.getHardware().getProcessor();

        long[] currentFreq = processor.getCurrentFreq();

        for (int i = 0; i < currentFreq.length; i++) {

            JTextField textField = processorTextFields.get(i);
            BigDecimal divide = new BigDecimal(currentFreq[i]).divide(new BigDecimal(1000000000), 2, RoundingMode.HALF_UP);
            String freqStr = divide + " GHz";
            textField.setText(freqStr);
            cpuForm.getFreqLabel().setText(freqStr);
        }

    }

    private static void initIndicatorInfo() {
        CpuForm cpuForm = getInstance();
        CentralProcessor processor = App.si.getHardware().getProcessor();
        cpuForm.getInterruptsLabel().setText(String.valueOf(processor.getInterrupts()));
        cpuForm.getContextSwitchesLabel().setText(String.valueOf(processor.getContextSwitches()));
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
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 0), -1, -1));
        splitPane1.setLeftComponent(panel2);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane1.setViewportView(panel3);
        scuPanel = new JPanel();
        scuPanel.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel3.add(scuPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scuTitleLabel = new JLabel();
        scuTitleLabel.setText("System CPU Usage");
        scuPanel.add(scuTitleLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scuProgressBar = new JProgressBar();
        scuPanel.add(scuProgressBar, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pcuPanel = new JPanel();
        pcuPanel.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel3.add(pcuPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pcuTitleLabel = new JLabel();
        pcuTitleLabel.setText("Processor CPU Usage");
        pcuPanel.add(pcuTitleLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pcuProgressBarPanel = new JPanel();
        pcuProgressBarPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        pcuPanel.add(pcuProgressBarPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel3.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pcfLabel = new JLabel();
        pcfLabel.setText("Processor CPU Frequency");
        panel1.add(pcfLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pcfPanel = new JPanel();
        pcfPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(pcfPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 1, new Insets(10, 0, 10, 10), -1, -1));
        splitPane1.setRightComponent(panel4);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel4.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cpuInfoTextPane = new JTextPane();
        cpuInfoTextPane.setEditable(true);
        panel5.add(cpuInfoTextPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 4, new Insets(10, 10, 10, 10), -1, -1));
        panel4.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Usage");
        panel6.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Freq");
        panel6.add(label2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        usageLabel = new JLabel();
        usageLabel.setText("Label");
        panel6.add(usageLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        freqLabel = new JLabel();
        freqLabel.setText("Label");
        panel6.add(freqLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Interrupts");
        panel6.add(label3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        InterruptsLabel = new JLabel();
        InterruptsLabel.setText("Label");
        panel6.add(InterruptsLabel, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Context Switches");
        panel6.add(label4, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contextSwitchesLabel = new JLabel();
        contextSwitchesLabel.setText("Label");
        panel6.add(contextSwitchesLabel, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
