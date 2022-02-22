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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;
import oshi.hardware.CentralProcessor;
import oshi.hardware.Sensors;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    private JScrollPane scrollPaneLeft;
    private JScrollPane scrollPaneRight;
    private JLabel temperatureLabel;
    private JLabel voltageLabel;
    private JLabel fanSpeedsLabel;
    private JSplitPane splitPane;
    private JPanel chartPanel;

    private static final Log logger = LogFactory.get();

    private static CpuForm cpuForm;

    private static long[] prevTicks;

    private static long[][] preProcessorTicks;

    private static List<JProgressBar> processorProgressBars;
    private static List<JTextField> processorTextFields;

    private static DynamicTimeSeriesCollection sysData;

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

        cpuForm.getSplitPane().setDividerLocation((int) (App.mainFrame.getWidth() * 0.38));

        Style.emphaticTitleFont(cpuForm.getScuTitleLabel());
        Style.emphaticTitleFont(cpuForm.getPcuTitleLabel());
        Style.emphaticTitleFont(cpuForm.getPcfLabel());

        Style.emphaticIndicatorFont(cpuForm.getUsageLabel());
        Style.emphaticIndicatorFont(cpuForm.getFreqLabel());
        Style.emphaticIndicatorFont(cpuForm.getInterruptsLabel());
        Style.emphaticIndicatorFont(cpuForm.getContextSwitchesLabel());
        Style.emphaticIndicatorFont(cpuForm.getTemperatureLabel());
        Style.emphaticIndicatorFont(cpuForm.getVoltageLabel());
        Style.emphaticIndicatorFont(cpuForm.getFanSpeedsLabel());

        ScrollUtil.smoothPane(cpuForm.getScrollPaneLeft());
        ScrollUtil.smoothPane(cpuForm.getScrollPaneRight());

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

        // Codes bellow are copied from oshi demo.
        Date date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        sysData = new DynamicTimeSeriesCollection(1, 60, new Second());
        sysData.setTimeBase(new Second(date));
        sysData.addSeries(floatArrayPercent(cpuData(cpu)), 0, "All cpus");

        JFreeChart systemCpu = ChartFactory.createTimeSeriesChart(null, null, null, sysData, false,
                true, false);

        systemCpu.setBackgroundPaint(cpuForm.getChartPanel().getBackground());
        systemCpu.setAntiAlias(true);
        systemCpu.getXYPlot().setDomainGridlinesVisible(false);
        systemCpu.getXYPlot().setRangeGridlinesVisible(false);
        systemCpu.getXYPlot().setBackgroundPaint(cpuForm.getScuProgressBar().getBackground());
        systemCpu.getXYPlot().setOutlinePaint(cpuForm.getScuProgressBar().getBackground());
        systemCpu.getXYPlot().getRenderer().setSeriesPaint(0, cpuForm.getScuProgressBar().getForeground());
        systemCpu.getXYPlot().getRenderer().setSeriesStroke(0, new BasicStroke(2f));
        systemCpu.getXYPlot().getDomainAxis().setVisible(false);
        systemCpu.getXYPlot().getRangeAxis().setVisible(false);

        cpuForm.getChartPanel().setPreferredSize(new Dimension(-1, 100));
        cpuForm.getChartPanel().setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        cpuForm.getChartPanel().add(new ChartPanel(systemCpu), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));


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
        builder.append("<br/>");
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

    /**
     * Codes are copied from Hutool and have some modifications.
     */
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

        sysData.advanceTime();
        sysData.appendData(floatArrayPercent(cpuData(processor)));

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
        BigDecimal totalFreq = BigDecimal.ZERO;

        for (int i = 0; i < currentFreq.length; i++) {

            JTextField textField = processorTextFields.get(i);
            BigDecimal divide = new BigDecimal(currentFreq[i]).divide(new BigDecimal(1000000000), 2, RoundingMode.HALF_UP);
            String freqStr = divide + " GHz";
            textField.setText(freqStr);

            totalFreq = divide.add(totalFreq);

        }
        cpuForm.getFreqLabel().setText(String.valueOf(totalFreq.divide(new BigDecimal(currentFreq.length), 2, RoundingMode.HALF_UP)));

    }

    private static void initIndicatorInfo() {
        CpuForm cpuForm = getInstance();
        CentralProcessor processor = App.si.getHardware().getProcessor();
        cpuForm.getInterruptsLabel().setText(String.valueOf(processor.getInterrupts()));
        cpuForm.getContextSwitchesLabel().setText(String.valueOf(processor.getContextSwitches()));

        Sensors sensors = App.si.getHardware().getSensors();

        cpuForm.getTemperatureLabel().setText(String.format("%.1f°C", sensors.getCpuTemperature()));
        // Tips are copied from oshi.
        cpuForm.getTemperatureLabel().setToolTipText("On Windows, if not running Open Hardware Monitor, \n" +
                "requires elevated permissions and hardware BIOS that supports publishing to WMI. \n" +
                "In this case, returns the temperature of the \"Thermal Zone\" \n" +
                "which may be different than CPU temperature obtained from other sources. \n" +
                "In addition, some motherboards may only refresh this value on certain events.");
        cpuForm.getVoltageLabel().setText(String.valueOf(sensors.getCpuVoltage()));
        cpuForm.getFanSpeedsLabel().setText(Arrays.toString(sensors.getFanSpeeds()));
    }

    /**
     * Codes are copied from oshi demo.
     *
     * @param d
     * @return
     */
    private static float[] floatArrayPercent(double d) {
        float[] f = new float[1];
        f[0] = (float) (100d * d);
        return f;
    }

    /**
     * Codes are copied from oshi demo.
     *
     * @param proc
     * @return
     */
    private static double cpuData(CentralProcessor proc) {
        if (prevTicks == null) {
            return 0;
        }
        double d = proc.getSystemCpuLoadBetweenTicks(prevTicks);
        return d;
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
        splitPane = new JSplitPane();
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(14);
        mainPanel.add(splitPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 0), -1, -1));
        splitPane.setLeftComponent(panel2);
        scrollPaneLeft = new JScrollPane();
        panel2.add(scrollPaneLeft, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(5, 1, new Insets(8, 0, 0, 0), -1, -1));
        scrollPaneLeft.setViewportView(panel3);
        scuPanel = new JPanel();
        scuPanel.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel3.add(scuPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scuTitleLabel = new JLabel();
        scuTitleLabel.setText("System CPU Usage");
        scuPanel.add(scuTitleLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scuProgressBar = new JProgressBar();
        scuPanel.add(scuProgressBar, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pcuPanel = new JPanel();
        pcuPanel.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel3.add(pcuPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pcuTitleLabel = new JLabel();
        pcuTitleLabel.setText("Processor CPU Usage");
        pcuPanel.add(pcuTitleLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pcuProgressBarPanel = new JPanel();
        pcuProgressBarPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        pcuPanel.add(pcuProgressBarPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel3.add(panel1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pcfLabel = new JLabel();
        pcfLabel.setText("Processor CPU Frequency");
        panel1.add(pcfLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pcfPanel = new JPanel();
        pcfPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(pcfPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        chartPanel = new JPanel();
        chartPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(chartPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 1, new Insets(10, 0, 10, 10), -1, -1));
        splitPane.setRightComponent(panel4);
        scrollPaneRight = new JScrollPane();
        panel4.add(scrollPaneRight, new GridConstraints(0, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPaneRight.setViewportView(panel5);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 10, 10, 10), -1, -1));
        panel5.add(panel6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        cpuInfoTextPane = new JTextPane();
        cpuInfoTextPane.setEditable(true);
        panel6.add(cpuInfoTextPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(6, 3, new Insets(10, 10, 10, 10), -1, -1));
        panel5.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(panel8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Usage");
        panel8.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        usageLabel = new JLabel();
        usageLabel.setText("Label");
        panel8.add(usageLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(panel9, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Freq");
        panel9.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        freqLabel = new JLabel();
        freqLabel.setText("Label");
        panel9.add(freqLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(2, 1, new Insets(10, 0, 0, 0), -1, -1));
        panel7.add(panel10, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Interrupts");
        panel10.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        InterruptsLabel = new JLabel();
        InterruptsLabel.setText("Label");
        panel10.add(InterruptsLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(2, 1, new Insets(10, 0, 0, 0), -1, -1));
        panel7.add(panel11, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Temperature");
        panel11.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        temperatureLabel = new JLabel();
        temperatureLabel.setText("Label");
        panel11.add(temperatureLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(2, 1, new Insets(10, 0, 0, 0), -1, -1));
        panel7.add(panel12, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Context Switches");
        panel12.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contextSwitchesLabel = new JLabel();
        contextSwitchesLabel.setText("Label");
        panel12.add(contextSwitchesLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(2, 1, new Insets(10, 0, 0, 0), -1, -1));
        panel7.add(panel13, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Voltage");
        panel13.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        voltageLabel = new JLabel();
        voltageLabel.setText("Label");
        panel13.add(voltageLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(2, 1, new Insets(10, 0, 0, 0), -1, -1));
        panel7.add(panel14, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Fan Speeds");
        panel14.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fanSpeedsLabel = new JLabel();
        fanSpeedsLabel.setText("Label");
        panel14.add(fanSpeedsLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel7.add(separator1, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator2 = new JSeparator();
        panel7.add(separator2, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator3 = new JSeparator();
        panel7.add(separator3, new GridConstraints(5, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
