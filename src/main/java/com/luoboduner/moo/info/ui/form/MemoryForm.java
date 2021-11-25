package com.luoboduner.moo.info.ui.form;

import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.util.ScrollUtil;
import lombok.Getter;
import oshi.hardware.GlobalMemory;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * MemoryForm
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/16.
 */
@Getter
public class MemoryForm {
    private static final Log logger = LogFactory.get();

    private static MemoryForm memoryForm;
    private JPanel mainPanel;
    private JProgressBar physicalMemoryProgressBar;
    private JProgressBar virtualMemoryProgressBar;
    private JLabel physicalMemoryTitle;
    private JLabel physicalMemoryUsed;
    private JLabel physicalMemoryAvailable;
    private JLabel virtualMemoryTitle;
    private JLabel virtualMemoryUsed;
    private JLabel virtualMemoryAvailable;
    private JLabel swapUsedLabel;
    private JProgressBar swapProgressBar;
    private JLabel swapAvailableLabel;
    private JLabel swapTitle;
    private JLabel jvmMemoryTitle;
    private JProgressBar jvmProgressBar;
    private JLabel jvmUsedLabel;
    private JLabel jvmAvailableLabel;
    private JLabel physicalMemoryInfoLabel;
    private JScrollPane scrollPane;
    private JTextPane physicalMemoryInfoTextPane;

    public static MemoryForm getInstance() {
        if (memoryForm == null) {
            memoryForm = new MemoryForm();
        }
        return memoryForm;
    }

    public static void init() {
        memoryForm = getInstance();

        initUi();
        initPhysicalMemoryInfo();

        ScheduledExecutorService serviceStartPerSecond = Executors.newSingleThreadScheduledExecutor();
        serviceStartPerSecond.scheduleAtFixedRate(MemoryForm::initMemoryProgressInfo, 0, 1, TimeUnit.SECONDS);

    }

    private static void initPhysicalMemoryInfo() {
        memoryForm.getPhysicalMemoryInfoTextPane().setText(getMemoryInfo());
    }

    private static void initUi() {
        MemoryForm memoryForm = getInstance();
        JPanel mainPanel = memoryForm.getMainPanel();
        Font emphaticFont = new Font(mainPanel.getFont().getName(), Font.BOLD, mainPanel.getFont().getSize() + 2);
        memoryForm.getPhysicalMemoryTitle().setFont(emphaticFont);
        memoryForm.getVirtualMemoryTitle().setFont(emphaticFont);
        memoryForm.getSwapTitle().setFont(emphaticFont);
        memoryForm.getJvmMemoryTitle().setFont(emphaticFont);
        memoryForm.getPhysicalMemoryInfoLabel().setFont(emphaticFont);

        Dimension d = new Dimension(-1, 100);
        memoryForm.getPhysicalMemoryProgressBar().setMinimumSize(d);

        ScrollUtil.smoothPane(memoryForm.getScrollPane());

        String contentType = "text/html; charset=utf-8";
        memoryForm.getPhysicalMemoryInfoTextPane().setContentType(contentType);

    }

    private static void initMemoryProgressInfo() {
        GlobalMemory memory = App.si.getHardware().getMemory();

        // global memory
        long total = memory.getTotal();
        long available = memory.getAvailable();
        MemoryForm memoryForm = getInstance();
        JProgressBar physicalMemoryProgressBar = memoryForm.getPhysicalMemoryProgressBar();
        physicalMemoryProgressBar.setMaximum(100);
        int usagePercent = (int) ((total - available) * 100 / total);
        physicalMemoryProgressBar.setValue(usagePercent);
        String progressStr = usagePercent + "%";
        physicalMemoryProgressBar.setToolTipText(progressStr);
        physicalMemoryProgressBar.setStringPainted(true);
        physicalMemoryProgressBar.setString(progressStr);

        memoryForm.getPhysicalMemoryUsed().setText("Used " + DataSizeUtil.format(total - available) + "/" + DataSizeUtil.format(total));
        memoryForm.getPhysicalMemoryAvailable().setText(DataSizeUtil.format(available) + "/" + DataSizeUtil.format(total) + " Available");

        // virtual memory
        VirtualMemory virtualMemory = memory.getVirtualMemory();
        long virtualMax = virtualMemory.getVirtualMax();
        long virtualInUse = virtualMemory.getVirtualInUse();
        JProgressBar virtualMemoryProgressBar = memoryForm.getVirtualMemoryProgressBar();
        virtualMemoryProgressBar.setMaximum(100);
        int virtualUsagePercent = (int) (virtualInUse * 100 / virtualMax);
        virtualMemoryProgressBar.setValue(virtualUsagePercent);
        virtualMemoryProgressBar.setToolTipText(virtualUsagePercent + "%");
        memoryForm.getVirtualMemoryUsed().setText("Used " + DataSizeUtil.format(virtualInUse) + "/" + DataSizeUtil.format(virtualMax));
        memoryForm.getVirtualMemoryAvailable().setText(DataSizeUtil.format(virtualMax - virtualInUse) + "/" + DataSizeUtil.format(virtualMax) + " Available");

        // swap memory
        long swapTotal = virtualMemory.getSwapTotal();
        long swapUsed = virtualMemory.getSwapUsed();
        JProgressBar swapProgressBar = memoryForm.getSwapProgressBar();
        swapProgressBar.setMaximum(100);

        int swapUsagePercent = 0;
        if (swapTotal != 0) {
            swapUsagePercent = (int) (swapUsed * 100 / swapTotal);

        }
        swapProgressBar.setValue(swapUsagePercent);
        swapProgressBar.setToolTipText(swapUsagePercent + "%");
        memoryForm.getSwapUsedLabel().setText("Used " + DataSizeUtil.format(swapUsed) + "/" + DataSizeUtil.format(swapTotal) + " | page in " + virtualMemory.getSwapPagesIn() + " page out " + virtualMemory.getSwapPagesOut());
        memoryForm.getSwapAvailableLabel().setText(DataSizeUtil.format(swapTotal - swapUsed) + "/" + DataSizeUtil.format(swapTotal) + " Available");

        long jvmTotal = Runtime.getRuntime().totalMemory();
        long jvmMax = Runtime.getRuntime().maxMemory();
        long jvmFree = Runtime.getRuntime().freeMemory();
        long jvmUsed = jvmTotal - jvmFree;

        JProgressBar jvmProgressBar = memoryForm.getJvmProgressBar();
        jvmProgressBar.setMaximum(100);
        int jvmUsagePercent = (int) (jvmUsed * 100 / jvmTotal);
        jvmProgressBar.setValue(jvmUsagePercent);
        jvmProgressBar.setToolTipText(jvmUsagePercent + "%");
        memoryForm.getJvmUsedLabel().setText("Used " + DataSizeUtil.format(jvmUsed) + " / Total " + DataSizeUtil.format(jvmTotal) + " / Max " + DataSizeUtil.format(jvmMax));
        memoryForm.getJvmAvailableLabel().setText("");

    }

    /**
     * @return
     */
    public static String getMemoryInfo() {
        StringBuilder builder = new StringBuilder();
        GlobalMemory globalMemory = App.si.getHardware().getMemory();

        builder.append("<br/>");
        builder.append("<b>Total: </b>").append(DataSizeUtil.format(globalMemory.getTotal()));
        builder.append("<br/><b>Page Size: </b>").append(DataSizeUtil.format(globalMemory.getPageSize()));
        builder.append("<br/>");

        List<PhysicalMemory> physicalMemories = globalMemory.getPhysicalMemory();
        for (int i = 0; i < physicalMemories.size(); i++) {
            PhysicalMemory physicalMemory = physicalMemories.get(i);

            builder.append("<br/><b>Physical Memory: </b>#").append(i);
            builder.append("<br/><b>BankLabel: </b>").append(physicalMemory.getBankLabel());
            builder.append("<br/><b>Manufacturer: </b>").append(physicalMemory.getManufacturer());
            builder.append("<br/><b>Capacity: </b>").append(DataSizeUtil.format(physicalMemory.getCapacity()));
            builder.append("<br/><b>Memory Type: </b>").append(physicalMemory.getMemoryType());
            builder.append("<br/><b>Clock Speed: </b>").append(new BigDecimal(physicalMemory.getClockSpeed()).divide(new BigDecimal(1000000), 0, RoundingMode.HALF_UP)).append("MHz");

            builder.append("<br/>");
        }

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
        mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        scrollPane = new JScrollPane();
        mainPanel.add(scrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane.setViewportView(panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 3, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        physicalMemoryTitle = new JLabel();
        physicalMemoryTitle.setText("Physical Memory");
        panel2.add(physicalMemoryTitle, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        physicalMemoryProgressBar = new JProgressBar();
        panel2.add(physicalMemoryProgressBar, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        physicalMemoryUsed = new JLabel();
        physicalMemoryUsed.setText("Used -");
        panel2.add(physicalMemoryUsed, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        physicalMemoryAvailable = new JLabel();
        physicalMemoryAvailable.setText("- Available");
        panel2.add(physicalMemoryAvailable, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 3, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        virtualMemoryTitle = new JLabel();
        virtualMemoryTitle.setText("Virtual Memory");
        panel3.add(virtualMemoryTitle, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        virtualMemoryProgressBar = new JProgressBar();
        panel3.add(virtualMemoryProgressBar, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        virtualMemoryUsed = new JLabel();
        virtualMemoryUsed.setText("Used -");
        panel3.add(virtualMemoryUsed, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        virtualMemoryAvailable = new JLabel();
        virtualMemoryAvailable.setText("- Available");
        panel3.add(virtualMemoryAvailable, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 3, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        swapTitle = new JLabel();
        swapTitle.setText("Swap");
        panel4.add(swapTitle, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        swapProgressBar = new JProgressBar();
        panel4.add(swapProgressBar, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        swapUsedLabel = new JLabel();
        swapUsedLabel.setText("Used -");
        panel4.add(swapUsedLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        swapAvailableLabel = new JLabel();
        swapAvailableLabel.setText("- Available");
        panel4.add(swapAvailableLabel, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel4.add(spacer3, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(3, 3, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        jvmMemoryTitle = new JLabel();
        jvmMemoryTitle.setText("Java Runtime Memory of MooInfo");
        panel5.add(jvmMemoryTitle, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        jvmProgressBar = new JProgressBar();
        panel5.add(jvmProgressBar, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        jvmUsedLabel = new JLabel();
        jvmUsedLabel.setText("Used -");
        panel5.add(jvmUsedLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        jvmAvailableLabel = new JLabel();
        jvmAvailableLabel.setText("- Available");
        panel5.add(jvmAvailableLabel, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel5.add(spacer4, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel1.add(spacer5, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel6, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        physicalMemoryInfoLabel = new JLabel();
        physicalMemoryInfoLabel.setText("Physical Memory Info");
        panel6.add(physicalMemoryInfoLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        physicalMemoryInfoTextPane = new JTextPane();
        panel6.add(physicalMemoryInfoTextPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel1.add(separator1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
