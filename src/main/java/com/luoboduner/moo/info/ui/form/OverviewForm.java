package com.luoboduner.moo.info.ui.form;

import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.common.collect.Maps;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.ui.Style;
import com.luoboduner.moo.info.util.ScrollUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;
import oshi.util.EdidUtil;
import oshi.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private JLabel diskStorageLabel;
    private JLabel diskStorageInfoLabel;
    private JLabel displayInfoLabel;
    private JLabel displayLabel;
    private JLabel soundCardLabel;
    private JLabel soundCardInfoLabel;
    private JLabel powerSourceLabel;
    private JLabel powerSourceInfoLabel;
    private JLabel firmwareInfoLabel;
    private JLabel firmwareLabel;
    private JScrollPane scrollPane;

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
        Style.emphaticLabelFont(overviewForm.getDiskStorageLabel());
        Style.emphaticLabelFont(overviewForm.getDisplayLabel());
        Style.emphaticLabelFont(overviewForm.getSoundCardLabel());
        Style.emphaticLabelFont(overviewForm.getPowerSourceLabel());
        Style.emphaticLabelFont(overviewForm.getFirmwareLabel());

        overviewForm.getCpuLabel().setIcon(new FlatSVGIcon("icons/cpu.svg", 20, 20));
        overviewForm.getMemoryLabel().setIcon(new FlatSVGIcon("icons/memory.svg", 20, 20));
        overviewForm.getGraphicsCardLabel().setIcon(new FlatSVGIcon("icons/graphicsCard.svg", 20, 20));
        overviewForm.getBaseBoardLabel().setIcon(new FlatSVGIcon("icons/chip.svg", 20, 20));
        overviewForm.getDiskStorageLabel().setIcon(new FlatSVGIcon("icons/storage.svg", 20, 20));
        overviewForm.getDisplayLabel().setIcon(new FlatSVGIcon("icons/display.svg", 20, 20));
        overviewForm.getSoundCardLabel().setIcon(new FlatSVGIcon("icons/sound.svg", 20, 20));
        overviewForm.getPowerSourceLabel().setIcon(new FlatSVGIcon("icons/battery.svg", 20, 20));
        overviewForm.getFirmwareLabel().setIcon(new FlatSVGIcon("icons/firmware.svg", 20, 20));

        ScrollUtil.smoothPane(overviewForm.getScrollPane());
    }

    private static void initInfo() {
        OverviewForm overviewForm = getInstance();

        HardwareAbstractionLayer hardware = App.si.getHardware();

        ComputerSystem computerSystem = hardware.getComputerSystem();
        StringBuilder deviceNameBuilder = new StringBuilder();
        deviceNameBuilder.append(computerSystem.getManufacturer());
        if (!"unknown".equalsIgnoreCase(computerSystem.getModel())) {
            deviceNameBuilder.append(" ").append(computerSystem.getModel());
        }
        overviewForm.getDeviceNameLabel().setText(deviceNameBuilder.toString());

        OperatingSystem operatingSystem = App.si.getOperatingSystem();
        overviewForm.getOsNameLabel().setText(operatingSystem.toString());

        overviewForm.getCpuInfo().setText(hardware.getProcessor().getProcessorIdentifier().getName());
        overviewForm.getMemoryInfo().setText(getMemoryInfo(hardware.getMemory()));
        overviewForm.getGraphicsCardInfo().setText(getGraphicsCardInfo(hardware));
        overviewForm.getBaseBoardInfoLabel().setText(getBaseBoardInfo(hardware.getComputerSystem().getBaseboard()));
        overviewForm.getDiskStorageInfoLabel().setText(getDiskStorageInfo(hardware));
        overviewForm.getDisplayInfoLabel().setText(getDisplayInfo(hardware));
        overviewForm.getSoundCardInfoLabel().setText(getSoundCardInfo(hardware));
        overviewForm.getPowerSourceInfoLabel().setText(getPowerSourceInfo(hardware));
        overviewForm.getFirmwareInfoLabel().setText(getFirmware(hardware));

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
            detailBuilder.append(" ").append(new BigDecimal(physicalMemory.getClockSpeed()).divide(new BigDecimal(1000000), 0, RoundingMode.HALF_UP)).append("MHz");
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

    /**
     * Disk Storage info text,like:"KBG40ZNV512G KIOXIA (标准磁盘驱动器) 512 GB"
     *
     * @param hardware
     * @return
     */
    private static String getDiskStorageInfo(HardwareAbstractionLayer hardware) {
        List<String> detailList = new ArrayList<>();
        StringBuilder detailBuilder;
        List<HWDiskStore> diskStores = hardware.getDiskStores();
        for (HWDiskStore hwDiskStore : diskStores) {
            detailBuilder = new StringBuilder();
            detailBuilder.append(hwDiskStore.getModel());
            detailBuilder.append(" ").append(hwDiskStore.getSize() > 0 ? FormatUtil.formatBytesDecimal(hwDiskStore.getSize()) : "?");

            detailList.add(detailBuilder.toString());
        }
        return StringUtils.join(detailList, " + ");
    }

    /**
     * Display Info
     *
     * @param hardware
     * @return
     */
    private static String getDisplayInfo(HardwareAbstractionLayer hardware) {
        List<String> detailList = new ArrayList<>();
        StringBuilder detailBuilder;

        List<Display> displays = hardware.getDisplays();
        for (Display display : displays) {
            detailBuilder = new StringBuilder();
            byte[] edid = display.getEdid();
            byte[][] desc = EdidUtil.getDescriptors(edid);
            Map<String, String> infoMap = Maps.newHashMap();
            for (byte[] b : desc) {
                int descriptorType = EdidUtil.getDescriptorType(b);
                if (descriptorType == 0xff || descriptorType == 0xfe || descriptorType == 0xfd || descriptorType == 0xfb || descriptorType == 0xfa) {
                } else if (descriptorType == 0xfc) {
                    infoMap.put("name", EdidUtil.getDescriptorText(b));
                } else {
                    if (EdidUtil.getDescriptorType(b) > 0x0f || EdidUtil.getDescriptorType(b) < 0x00) {
                        infoMap.put("size", EdidUtil.getTimingDescriptor(b));
                    }
                }
            }

            detailBuilder.append(infoMap.get("name"));
            detailBuilder.append(" ").append(infoMap.get("size"));

            detailList.add(detailBuilder.toString());

        }

        return StringUtils.join(detailList, " + ");
    }

    /**
     * @param hardware
     * @return
     */
    private static String getSoundCardInfo(HardwareAbstractionLayer hardware) {
        List<String> detailList = new ArrayList<>();
        StringBuilder detailBuilder;
        List<SoundCard> soundCards = hardware.getSoundCards();
        for (SoundCard soundCard : soundCards) {
            detailBuilder = new StringBuilder();
            detailBuilder.append(soundCard.getName());

            detailList.add(detailBuilder.toString());
        }

        return StringUtils.join(detailList, " + ");
    }

    /**
     * PowerSourceInfo
     *
     * @param hardware
     * @return
     */
    private static String getPowerSourceInfo(HardwareAbstractionLayer hardware) {
        List<String> detailList = new ArrayList<>();
        StringBuilder detailBuilder;
        List<PowerSource> powerSources = hardware.getPowerSources();
        for (PowerSource powerSource : powerSources) {
            detailBuilder = new StringBuilder();
            detailBuilder.append(powerSource.getName());
            detailBuilder.append(" ").append(powerSource.getManufacturer());
            detailBuilder.append(" ").append(powerSource.getDeviceName());
            if (!"unknown".equals(powerSource.getChemistry())) {
                detailBuilder.append(" ").append(powerSource.getChemistry());
            }
            detailBuilder.append(" ").append(powerSource.getMaxCapacity()).append("/").append(powerSource.getDesignCapacity());
            detailBuilder.append("(").append(powerSource.getCapacityUnits()).append(")");

            detailList.add(detailBuilder.toString());
        }

        return StringUtils.join(detailList, " + ");
    }

    /**
     * Firmware
     *
     * @param hardware
     * @return
     */
    private static String getFirmware(HardwareAbstractionLayer hardware) {
        Firmware firmware = hardware.getComputerSystem().getFirmware();

        StringBuilder detailBuilder = new StringBuilder();
        detailBuilder.append(firmware.getManufacturer());
        detailBuilder.append(" ").append(firmware.getName());
        detailBuilder.append(" ").append(firmware.getDescription());
        detailBuilder.append(" ").append(firmware.getVersion());
        detailBuilder.append(" ").append(firmware.getReleaseDate());

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
        mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        scrollPane = new JScrollPane();
        mainPanel.add(scrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane.setViewportView(panel1);
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
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(9, 3, new Insets(10, 20, 10, 10), -1, -1));
        panel1.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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
        diskStorageInfoLabel = new JLabel();
        diskStorageInfoLabel.setText("DiskStorage info");
        panel5.add(diskStorageInfoLabel, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        displayInfoLabel = new JLabel();
        displayInfoLabel.setText("Display info");
        panel5.add(displayInfoLabel, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        soundCardInfoLabel = new JLabel();
        soundCardInfoLabel.setText("SoundCard info");
        panel5.add(soundCardInfoLabel, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        powerSourceInfoLabel = new JLabel();
        powerSourceInfoLabel.setText("PowerSource info");
        panel5.add(powerSourceInfoLabel, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        firmwareInfoLabel = new JLabel();
        firmwareInfoLabel.setText("Firmware info");
        panel5.add(firmwareInfoLabel, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel5.add(spacer4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(5, 0, 5, 0), -1, -1));
        panel5.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cpuLabel = new JLabel();
        cpuLabel.setText("CPU");
        panel6.add(cpuLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(5, 0, 5, 0), -1, -1));
        panel5.add(panel7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        memoryLabel = new JLabel();
        memoryLabel.setText("Memory");
        panel7.add(memoryLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(5, 0, 5, 0), -1, -1));
        panel5.add(panel8, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        graphicsCardLabel = new JLabel();
        graphicsCardLabel.setText("GraphicsCard");
        panel8.add(graphicsCardLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(5, 0, 5, 0), -1, -1));
        panel5.add(panel9, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        baseBoardLabel = new JLabel();
        baseBoardLabel.setText("BaseBoard");
        panel9.add(baseBoardLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 1, new Insets(5, 0, 5, 0), -1, -1));
        panel5.add(panel10, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        diskStorageLabel = new JLabel();
        diskStorageLabel.setText("DiskStorage");
        panel10.add(diskStorageLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 1, new Insets(5, 0, 5, 0), -1, -1));
        panel5.add(panel11, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        displayLabel = new JLabel();
        displayLabel.setText("Display");
        panel11.add(displayLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 1, new Insets(5, 0, 5, 0), -1, -1));
        panel5.add(panel12, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        soundCardLabel = new JLabel();
        soundCardLabel.setText("SoundCard");
        panel12.add(soundCardLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 1, new Insets(5, 0, 5, 0), -1, -1));
        panel5.add(panel13, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        powerSourceLabel = new JLabel();
        powerSourceLabel.setText("PowerSource");
        panel13.add(powerSourceLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 1, new Insets(5, 0, 5, 0), -1, -1));
        panel5.add(panel14, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        firmwareLabel = new JLabel();
        firmwareLabel.setText("Firmware");
        panel14.add(firmwareLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 1, new Insets(0, 10, 0, 10), -1, -1));
        panel1.add(panel15, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel15.add(separator1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
