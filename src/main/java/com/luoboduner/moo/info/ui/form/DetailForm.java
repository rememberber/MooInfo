package com.luoboduner.moo.info.ui.form;

import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.ui.Style;
import com.luoboduner.moo.info.util.DateTimeUtil;
import com.luoboduner.moo.info.util.ScrollUtil;
import lombok.Getter;
import org.apache.commons.lang3.time.DateFormatUtils;
import oshi.hardware.*;
import oshi.software.os.NetworkParams;
import oshi.software.os.OperatingSystem;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

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

        detailForm.getOsLabel().setIcon(new FlatSVGIcon("icons/system.svg"));
        detailForm.getComputerLabel().setIcon(new FlatSVGIcon("icons/firmware.svg"));
        detailForm.getCpuLabel().setIcon(new FlatSVGIcon("icons/cpu.svg"));
        detailForm.getMemoryLabel().setIcon(new FlatSVGIcon("icons/memory.svg"));
        detailForm.getGraphicsCardLabel().setIcon(new FlatSVGIcon("icons/graphicsCard.svg"));
        detailForm.getBaseBoardLabel().setIcon(new FlatSVGIcon("icons/chip.svg"));
        detailForm.getStorageLabel().setIcon(new FlatSVGIcon("icons/storage.svg"));
        detailForm.getDisplayLabel().setIcon(new FlatSVGIcon("icons/display.svg"));
        detailForm.getSoundCardLabel().setIcon(new FlatSVGIcon("icons/sound.svg"));
        detailForm.getNetworkLabel().setIcon(new FlatSVGIcon("icons/network.svg"));
        detailForm.getPowerSourceLabel().setIcon(new FlatSVGIcon("icons/battery.svg"));

        String contentType = "text/html; charset=utf-8";
        detailForm.getOsTextPane().setContentType(contentType);
        detailForm.getComputerTextPane().setContentType(contentType);
        detailForm.getBaseBoardTextPane().setContentType(contentType);
        detailForm.getCpuTextPane().setContentType(contentType);
        detailForm.getMemoryTextPane().setContentType(contentType);
        detailForm.getStorageTextPane().setContentType(contentType);
        detailForm.getGraphicsCardTextPane().setContentType(contentType);
        detailForm.getDisplayTextPane().setContentType(contentType);
        detailForm.getSoundCardTextPane().setContentType(contentType);
        detailForm.getNetworkTextPane().setContentType(contentType);
        detailForm.getPowerSourceTextPane().setContentType(contentType);
    }

    private static void initInfo() {
        DetailForm detailForm = getInstance();

        HardwareAbstractionLayer hardware = App.si.getHardware();

        detailForm.getOsTextPane().setText(getOsInfo());
        detailForm.getComputerTextPane().setText(getComputerInfo());
        detailForm.getBaseBoardTextPane().setText(getBaseBoardInfo());
        detailForm.getCpuTextPane().setText(CpuForm.getCpuInfo());
        detailForm.getMemoryTextPane().setText(MemoryForm.getMemoryInfo());
        detailForm.getStorageTextPane().setText(getStorageInfo());
        detailForm.getGraphicsCardTextPane().setText(getGraphicsCardsInfo());
        detailForm.getDisplayTextPane().setText(getDisplayInfo());
        detailForm.getSoundCardTextPane().setText(getSoundCardsInfo());
        detailForm.getNetworkTextPane().setText(getNetworkInfo());
        detailForm.getPowerSourceTextPane().setText(PowerSourceForm.getPowerInfoText(hardware.getPowerSources()));
    }

    private static String getOsInfo() {
        StringBuilder builder = new StringBuilder();
        OperatingSystem operatingSystem = App.si.getOperatingSystem();

        builder.append("<b>Manufacturer: </b>").append(operatingSystem.getManufacturer());
        builder.append("<br/><b>Family: </b>").append(operatingSystem.getFamily());
        builder.append("<br/><b>Version: </b>").append(operatingSystem.getVersionInfo());
        builder.append("<br/><b>Bitness: </b>").append(operatingSystem.getBitness());
        builder.append("<br/>");
        builder.append("<br/><b>Max File Descriptors: </b>").append(operatingSystem.getFileSystem().getMaxFileDescriptors());
        builder.append("<br/><b>Open File Descriptors: </b>").append(operatingSystem.getFileSystem().getOpenFileDescriptors());
        builder.append("<br/><b>Thread Count: </b>").append(operatingSystem.getThreadCount());
        builder.append("<br/><b>Process Count: </b>").append(operatingSystem.getProcessCount());
        builder.append("<br/><b>System Boot Time: </b>").append(DateFormatUtils.format(operatingSystem.getSystemBootTime() * 1000, "yyyy-MM-dd HH:mm:ss"));
        builder.append("<br/><b>System Uptime: </b>").append(DateTimeUtil.toReadableTime(operatingSystem.getSystemUptime()));

        return builder.toString();
    }

    private static String getComputerInfo() {
        StringBuilder builder = new StringBuilder();
        ComputerSystem computerSystem = App.si.getHardware().getComputerSystem();

        builder.append("<b>Manufacturer: </b>").append(computerSystem.getManufacturer());
        builder.append("<br/><b>Model: </b>").append(computerSystem.getModel());
        builder.append("<br/><b>Serial Number: </b>").append(computerSystem.getSerialNumber());
        builder.append("<br/><b>Hardware UUID: </b>").append(computerSystem.getHardwareUUID());
        builder.append("<br/>");
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

        builder.append("<b>Manufacturer: </b>").append(baseboard.getManufacturer());
        builder.append("<br/><b>Model: </b>").append(baseboard.getModel());
        builder.append("<br/><b>Version: </b>").append(baseboard.getVersion());
        builder.append("<br/><b>SerialNumber: </b>").append(baseboard.getSerialNumber());

        return builder.toString();
    }

    private static String getStorageInfo() {
        StringBuilder builder = new StringBuilder();
        List<HWDiskStore> diskStores = App.si.getHardware().getDiskStores();
        for (int i = 0; i < diskStores.size(); i++) {
            HWDiskStore hwDiskStore = diskStores.get(i);

            builder.append("<b>Disk Store: </b>#").append(i);
            builder.append("<br/><b>Name: </b>").append(hwDiskStore.getName());
            builder.append("<br/><b>Model: </b>").append(hwDiskStore.getModel());
            builder.append("<br/><b>Serial: </b>").append(hwDiskStore.getSerial());
            builder.append("<br/><b>Size: </b>").append(DataSizeUtil.format(hwDiskStore.getSize()));
            builder.append("<br/><b>Partitions: </b>");
            for (HWPartition partition : hwDiskStore.getPartitions()) {
                builder.append("<br/>");
                builder.append(partition.toString());
            }

            builder.append("<br/>");
            builder.append("<br/>");

        }

        return builder.toString();
    }

    private static String getGraphicsCardsInfo() {
        StringBuilder builder = new StringBuilder();
        List<GraphicsCard> graphicsCards = App.si.getHardware().getGraphicsCards();

        for (int i = 0; i < graphicsCards.size(); i++) {
            GraphicsCard graphicsCard = graphicsCards.get(i);

            builder.append("<b>Graphics Card: </b>#").append(i);
            builder.append("<br/><b>Name: </b>").append(graphicsCard.getName());
            builder.append("<br/><b>Vendor: </b>").append(graphicsCard.getVendor());
            builder.append("<br/><b>Version: </b>").append(graphicsCard.getVersionInfo());
            builder.append("<br/><b>Device Id: </b>").append(graphicsCard.getDeviceId());
            builder.append("<br/><b>VRam: </b>").append(DataSizeUtil.format(graphicsCard.getVRam()));
            builder.append("<br/>");
            builder.append("<br/>");
        }

        return builder.toString();
    }

    private static String getDisplayInfo() {
        StringBuilder builder = new StringBuilder();
        List<Display> displays = App.si.getHardware().getDisplays();

        for (int i = 0; i < displays.size(); i++) {
            Display display = displays.get(i);

            builder.append("<b>Display: </b>#").append(i);
            builder.append("<br/>");
            builder.append(display.toString().replaceAll("\n", "<br/>"));
            builder.append("<br/>");
            builder.append("<br/>");
        }

        return builder.toString();
    }

    private static String getSoundCardsInfo() {
        StringBuilder builder = new StringBuilder();
        List<SoundCard> soundCards = App.si.getHardware().getSoundCards();

        for (int i = 0; i < soundCards.size(); i++) {
            SoundCard soundCard = soundCards.get(i);

            builder.append("<b>SoundCard: </b>#").append(i);
            builder.append("<br/><b>Name: </b>").append(soundCard.getName());
            builder.append("<br/><b>Codec: </b>").append(soundCard.getCodec());
            builder.append("<br/><b>Driver Version: </b>").append(soundCard.getDriverVersion());

            builder.append("<br/>");
            builder.append("<br/>");
        }

        return builder.toString();
    }

    private static String getNetworkInfo() {
        StringBuilder builder = new StringBuilder();
        NetworkParams networkParams = App.si.getOperatingSystem().getNetworkParams();
        builder.append("<b>Domain Name: </b>").append(networkParams.getDomainName());
        builder.append("<br/><b>Host Name: </b>").append(networkParams.getHostName());
        builder.append("<br/><b>Ipv4 Default Gateway: </b>").append(networkParams.getIpv4DefaultGateway());
        builder.append("<br/><b>Ipv6 Default Gateway: </b>").append(networkParams.getIpv6DefaultGateway());
        builder.append("<br/><b>Dns Servers: </b>").append(Arrays.toString(networkParams.getDnsServers()));
        builder.append("<br/>");
        builder.append("<br/>");

        List<NetworkIF> networkIFs = App.si.getHardware().getNetworkIFs(true);

        for (int i = 0; i < networkIFs.size(); i++) {
            NetworkIF networkIF = networkIFs.get(i);

            builder.append("<b>Network Interface: </b>#").append(i);
            builder.append("<br/>");
            builder.append(networkIF.toString().replaceAll("\n", "<br/>"));
            builder.append("<br/>");
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
        panel1.setLayout(new GridLayoutManager(12, 1, new Insets(0, 0, 0, 0), -1, -1));
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
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
