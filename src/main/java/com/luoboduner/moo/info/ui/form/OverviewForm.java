package com.luoboduner.moo.info.ui.form;

import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.ui.Style;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * OverviewForm
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/12.
 */
@Getter
public class OverviewForm {
    private static final Log logger = LogFactory.get();

    private static OverviewForm overviewForm;
    private JPanel mainPanel;
    private JLabel deviceNameLabel;
    private JLabel osNameLabel;
    private JLabel cpuLabel;
    private JLabel cpuInfo;
    private JLabel memoryLabel;
    private JLabel memoryInfo;
    private JLabel graphicsCardLabel;
    private JLabel graphicsCardInfo;
    private JLabel baseBoardLabel;
    private JLabel baseBoardInfoLabel;

    public static OverviewForm getInstance() {
        if (overviewForm == null) {
            overviewForm = new OverviewForm();
        }
        return overviewForm;
    }

    public static void init() {
        overviewForm = getInstance();

        initUi();
        initInfo();
    }

    private static void initUi() {
        OverviewForm overviewForm = getInstance();

        Style.emphaticIndicatorFont(overviewForm.getDeviceNameLabel());
        Style.emphaticIndicatorFont(overviewForm.getOsNameLabel());

        Style.emphaticLabelFont(overviewForm.getCpuLabel());
        Style.emphaticLabelFont(overviewForm.getMemoryLabel());
        Style.emphaticLabelFont(overviewForm.getGraphicsCardLabel());
        Style.emphaticLabelFont(overviewForm.getBaseBoardLabel());
    }

    private static void initInfo() {
        OverviewForm overviewForm = getInstance();

        HardwareAbstractionLayer hardware = App.si.getHardware();

        ComputerSystem computerSystem = hardware.getComputerSystem();
        overviewForm.getDeviceNameLabel().setText(computerSystem.getManufacturer());

        OperatingSystem operatingSystem = App.si.getOperatingSystem();
        overviewForm.getOsNameLabel().setText(operatingSystem.toString());

        overviewForm.getCpuInfo().setText(hardware.getProcessor().getProcessorIdentifier().getName());
        overviewForm.getMemoryInfo().setText(getMemoryInfo(hardware.getMemory()));
        overviewForm.getGraphicsCardInfo().setText(getGraphicsCardInfo(hardware));
        overviewForm.getBaseBoardInfoLabel().setText(getBaseBoardInfo(hardware.getComputerSystem().getBaseboard()));
    }

    /**
     * memory info text,like:"16 GB (SamSung DDR4 3200MHZ 8GB + SamSung DDR4 3200MHZ 8GB)"
     *
     * @param memory
     * @return
     */
    private static String getMemoryInfo(GlobalMemory memory) {
        StringBuilder memoryInfoBuilder = new StringBuilder();

        long totalCapacity = 0;
        List<String> detailList = new ArrayList<>();

        List<PhysicalMemory> physicalMemories = memory.getPhysicalMemory();
        StringBuilder detailBuilder;
        for (PhysicalMemory physicalMemory : physicalMemories) {
            detailBuilder = new StringBuilder();
            totalCapacity += physicalMemory.getCapacity();
            detailBuilder.append(physicalMemory.getManufacturer());
            detailBuilder.append(" ").append(physicalMemory.getMemoryType());
            detailBuilder.append(" ").append(new BigDecimal(physicalMemory.getClockSpeed()).divide(new BigDecimal(1000000), 0, RoundingMode.HALF_UP)).append("MHZ");
            detailBuilder.append(" ").append(DataSizeUtil.format(physicalMemory.getCapacity()));
            detailList.add(detailBuilder.toString());
        }
        memoryInfoBuilder.append(DataSizeUtil.format(totalCapacity));
        memoryInfoBuilder.append(" (").append(StringUtils.join(detailList, " + ")).append(")");

        return memoryInfoBuilder.toString();
    }

    /**
     * GraphicsCard info text,like:"NVIDIA GeForce MX450 8 GB + Intel(R) Iris(R) Xe Graphics 8 GB"
     *
     * @param hardware
     * @return
     */
    private static String getGraphicsCardInfo(HardwareAbstractionLayer hardware) {
        List<String> detailList = new ArrayList<>();
        StringBuilder detailBuilder;
        List<GraphicsCard> graphicsCards = hardware.getGraphicsCards();
        for (GraphicsCard graphicsCard : graphicsCards) {
            detailBuilder = new StringBuilder();
            detailBuilder.append(graphicsCard.getName());
            detailBuilder.append(" ").append(DataSizeUtil.format(graphicsCard.getVRam()));

            detailList.add(detailBuilder.toString());
        }
        return StringUtils.join(detailList, " + ");
    }

    /**
     * @return
     */
    private static String getBaseBoardInfo(Baseboard baseboard) {
        StringBuilder detailBuilder = new StringBuilder();
        detailBuilder.append(baseboard.getManufacturer());
        if (!"unknown".equals(baseboard.getModel())) {
            detailBuilder.append(" ").append(baseboard.getModel());
        }
        if (!"unknown".equals(baseboard.getVersion())) {
            detailBuilder.append(" ").append(baseboard.getVersion());
        }
        return detailBuilder.toString();
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
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 1, new Insets(10, 10, 10, 10), -1, -1));
        scrollPane1.setViewportView(panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        deviceNameLabel = new JLabel();
        deviceNameLabel.setText("Device Name");
        panel3.add(deviceNameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        panel2.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        osNameLabel = new JLabel();
        osNameLabel.setText("OS Name");
        panel4.add(osNameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel1.add(separator1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(4, 3, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cpuLabel = new JLabel();
        cpuLabel.setText("CPU");
        panel5.add(cpuLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        memoryLabel = new JLabel();
        memoryLabel.setText("Memory");
        panel5.add(memoryLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        graphicsCardLabel = new JLabel();
        graphicsCardLabel.setText("GraphicsCard");
        panel5.add(graphicsCardLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        baseBoardLabel = new JLabel();
        baseBoardLabel.setText("BaseBoard");
        panel5.add(baseBoardLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cpuInfo = new JLabel();
        cpuInfo.setText("CPU info");
        panel5.add(cpuInfo, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        memoryInfo = new JLabel();
        memoryInfo.setText("Memory info");
        panel5.add(memoryInfo, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        graphicsCardInfo = new JLabel();
        graphicsCardInfo.setText("GraphicsCard info");
        panel5.add(graphicsCardInfo, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        baseBoardInfoLabel = new JLabel();
        baseBoardInfoLabel.setText("BaseBoard info");
        panel5.add(baseBoardInfoLabel, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel5.add(spacer4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
