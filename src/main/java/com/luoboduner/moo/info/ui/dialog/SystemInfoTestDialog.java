package com.luoboduner.moo.info.ui.dialog;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.util.ComponentUtil;
import com.luoboduner.moo.info.util.ConsoleUtil;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;
import oshi.util.FormatUtil;
import oshi.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SystemInfoTestDialog extends JDialog {
    private static final Log logger = LogFactory.get();

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea textArea1;

    public SystemInfoTestDialog() {
        super(App.mainFrame, "System Info Test");
        ComponentUtil.setPreferSizeAndLocateToCenter(this, App.mainFrame.getWidth(), App.mainFrame.getHeight());
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        ThreadUtil.execute(this::sysInfoTest);
    }

    private void sysInfoTest() {
        logger.info("Initializing System...");
        SystemInfo si = new SystemInfo();

        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        printOperatingSystem(os);
        textArea1.append("\n");

        logger.info("Checking computer system...");
        printComputerSystem(hal.getComputerSystem());
        textArea1.append("\n");

        logger.info("Checking Processor...");
        printProcessor(hal.getProcessor());
        textArea1.append("\n");

        logger.info("Checking Memory...");
        printMemory(hal.getMemory());
        textArea1.append("\n");

        logger.info("Checking CPU...");
        printCpu(hal.getProcessor());
        textArea1.append("\n");

        logger.info("Checking Processes...");
        printProcesses(os, hal.getMemory());
        textArea1.append("\n");

        logger.info("Checking Services...");
        printServices(os);
        textArea1.append("\n");

        logger.info("Checking Sensors...");
        printSensors(hal.getSensors());
        textArea1.append("\n");

        logger.info("Checking Power sources...");
        printPowerSources(hal.getPowerSources());
        textArea1.append("\n");

        logger.info("Checking Disks...");
        printDisks(hal.getDiskStores());
        textArea1.append("\n");

        logger.info("Checking Logical Volume Groups ...");
        printLVgroups(hal.getLogicalVolumeGroups());
        textArea1.append("\n");

        logger.info("Checking File System...");
        printFileSystem(os.getFileSystem());
        textArea1.append("\n");

        logger.info("Checking Network interfaces...");
        printNetworkInterfaces(hal.getNetworkIFs());
        textArea1.append("\n");

        logger.info("Checking Network parameters...");
        printNetworkParameters(os.getNetworkParams());
        textArea1.append("\n");

        logger.info("Checking IP statistics...");
        printInternetProtocolStats(os.getInternetProtocolStats());
        textArea1.append("\n");

        logger.info("Checking Displays...");
        printDisplays(hal.getDisplays());
        textArea1.append("\n");

        logger.info("Checking USB Devices...");
        printUsbDevices(hal.getUsbDevices(true));
        textArea1.append("\n");

        logger.info("Checking Sound Cards...");
        printSoundCards(hal.getSoundCards());
        textArea1.append("\n");

        logger.info("Checking Graphics Cards...");
        printGraphicsCards(hal.getGraphicsCards());

        JOptionPane.showMessageDialog(contentPane, "Done!\n", "Finished!", JOptionPane.PLAIN_MESSAGE);
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    // all functions codes bellow are copied from oshi-core, and have some changed
    private void printOperatingSystem(final OperatingSystem os) {

        ConsoleUtil.consoleOnly(textArea1, String.valueOf(os));
        ConsoleUtil.consoleOnly(textArea1, "Booted: " + Instant.ofEpochSecond(os.getSystemBootTime()));
        ConsoleUtil.consoleOnly(textArea1, "Uptime: " + FormatUtil.formatElapsedSecs(os.getSystemUptime()));
        ConsoleUtil.consoleOnly(textArea1, "Running with" + (os.isElevated() ? "" : "out") + " elevated permissions.");
        ConsoleUtil.consoleOnly(textArea1, "Sessions:");
        for (OSSession s : os.getSessions()) {
            ConsoleUtil.consoleOnly(textArea1, " " + s.toString());
        }
    }

    private void printComputerSystem(final ComputerSystem computerSystem) {
        ConsoleUtil.consoleOnly(textArea1, "System: " + computerSystem.toString());
        ConsoleUtil.consoleOnly(textArea1, " Firmware: " + computerSystem.getFirmware().toString());
        ConsoleUtil.consoleOnly(textArea1, " Baseboard: " + computerSystem.getBaseboard().toString());
    }

    private void printProcessor(CentralProcessor processor) {
        ConsoleUtil.consoleOnly(textArea1, processor.toString());
    }

    private void printMemory(GlobalMemory memory) {
        ConsoleUtil.consoleOnly(textArea1, "Physical Memory: \n " + memory.toString());
        VirtualMemory vm = memory.getVirtualMemory();
        ConsoleUtil.consoleOnly(textArea1, "Virtual Memory: \n " + vm.toString());
        List<PhysicalMemory> pmList = memory.getPhysicalMemory();
        if (!pmList.isEmpty()) {
            ConsoleUtil.consoleOnly(textArea1, "Physical Memory: ");
            for (PhysicalMemory pm : pmList) {
                ConsoleUtil.consoleOnly(textArea1, " " + pm.toString());
            }
        }
    }

    private void printCpu(CentralProcessor processor) {
        ConsoleUtil.consoleOnly(textArea1, "Context Switches/Interrupts: " + processor.getContextSwitches() + " / " + processor.getInterrupts());

        long[] prevTicks = processor.getSystemCpuLoadTicks();
        long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
        ConsoleUtil.consoleOnly(textArea1, "CPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks));
        // Wait a second...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        ConsoleUtil.consoleOnly(textArea1, "CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks));
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long sys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

        ConsoleUtil.consoleOnly(textArea1, String.format(
                "User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%",
                100d * user / totalCpu, 100d * nice / totalCpu, 100d * sys / totalCpu, 100d * idle / totalCpu,
                100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu, 100d * steal / totalCpu));
        ConsoleUtil.consoleOnly(textArea1, String.format("CPU load: %.1f%%", processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100));
        double[] loadAverage = processor.getSystemLoadAverage(3);
        ConsoleUtil.consoleOnly(textArea1, "CPU load averages:" + (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0]))
                + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));
        // per core CPU
        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
        double[] load = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
        for (double avg : load) {
            procCpu.append(String.format(" %.1f%%", avg * 100));
        }
        ConsoleUtil.consoleOnly(textArea1, procCpu.toString());
        long freq = processor.getProcessorIdentifier().getVendorFreq();
        if (freq > 0) {
            ConsoleUtil.consoleOnly(textArea1, "Vendor Frequency: " + FormatUtil.formatHertz(freq));
        }
        freq = processor.getMaxFreq();
        if (freq > 0) {
            ConsoleUtil.consoleOnly(textArea1, "Max Frequency: " + FormatUtil.formatHertz(freq));
        }
        long[] freqs = processor.getCurrentFreq();
        if (freqs[0] > 0) {
            StringBuilder sb = new StringBuilder("Current Frequencies: ");
            for (int i = 0; i < freqs.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(FormatUtil.formatHertz(freqs[i]));
            }
            ConsoleUtil.consoleOnly(textArea1, sb.toString());
        }
    }

    private void printProcesses(OperatingSystem os, GlobalMemory memory) {
        OSProcess myProc = os.getProcess(os.getProcessId());
        // current process will never be null. Other code should check for null here
        ConsoleUtil.consoleOnly(textArea1,
                "My PID: " + myProc.getProcessID() + " with affinity " + Long.toBinaryString(myProc.getAffinityMask()));
        ConsoleUtil.consoleOnly(textArea1, "Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
        // Sort by highest CPU
        List<OSProcess> procs = os.getProcesses(OperatingSystem.ProcessFiltering.ALL_PROCESSES, OperatingSystem.ProcessSorting.CPU_DESC, 5);
        ConsoleUtil.consoleOnly(textArea1, "   PID  %CPU %MEM       VSZ       RSS Name");
        for (int i = 0; i < procs.size() && i < 5; i++) {
            OSProcess p = procs.get(i);
            ConsoleUtil.consoleOnly(textArea1, String.format(" %5d %5.1f %4.1f %9s %9s %s", p.getProcessID(),
                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName()));
        }
        OSProcess p = os.getProcess(os.getProcessId());
        ConsoleUtil.consoleOnly(textArea1, "Current process arguments: ");
        for (String s : p.getArguments()) {
            ConsoleUtil.consoleOnly(textArea1, "  " + s);
        }
        ConsoleUtil.consoleOnly(textArea1, "Current process environment: ");
        for (Map.Entry<String, String> e : p.getEnvironmentVariables().entrySet()) {
            ConsoleUtil.consoleOnly(textArea1, "  " + e.getKey() + "=" + e.getValue());
        }
    }

    private void printServices(OperatingSystem os) {
        ConsoleUtil.consoleOnly(textArea1, "Services: ");
        ConsoleUtil.consoleOnly(textArea1, "   PID   State   Name");
        // DO 5 each of running and stopped
        int i = 0;
        for (OSService s : os.getServices()) {
            if (s.getState().equals(OSService.State.RUNNING) && i++ < 5) {
                ConsoleUtil.consoleOnly(textArea1, String.format(" %5d  %7s  %s", s.getProcessID(), s.getState(), s.getName()));
            }
        }
        i = 0;
        for (OSService s : os.getServices()) {
            if (s.getState().equals(OSService.State.STOPPED) && i++ < 5) {
                ConsoleUtil.consoleOnly(textArea1, String.format(" %5d  %7s  %s", s.getProcessID(), s.getState(), s.getName()));
            }
        }
    }

    private void printSensors(Sensors sensors) {
        ConsoleUtil.consoleOnly(textArea1, "Sensors: " + sensors.toString());
    }

    private void printPowerSources(List<PowerSource> list) {
        StringBuilder sb = new StringBuilder("Power Sources: ");
        if (list.isEmpty()) {
            sb.append("Unknown");
        }
        for (PowerSource powerSource : list) {
            sb.append("\n ").append(powerSource.toString());
        }
        ConsoleUtil.consoleOnly(textArea1, sb.toString());
    }

    private void printDisks(List<HWDiskStore> list) {
        ConsoleUtil.consoleOnly(textArea1, "Disks:");
        for (HWDiskStore disk : list) {
            ConsoleUtil.consoleOnly(textArea1, " " + disk.toString());

            List<HWPartition> partitions = disk.getPartitions();
            for (HWPartition part : partitions) {
                ConsoleUtil.consoleOnly(textArea1, " |-- " + part.toString());
            }
        }

    }

    private void printLVgroups(List<LogicalVolumeGroup> list) {
        if (!list.isEmpty()) {
            ConsoleUtil.consoleOnly(textArea1, "Logical Volume Groups:");
            for (LogicalVolumeGroup lvg : list) {
                ConsoleUtil.consoleOnly(textArea1, " " + lvg.toString());
            }
        }
    }

    private void printFileSystem(FileSystem fileSystem) {
        ConsoleUtil.consoleOnly(textArea1, "File System:");

        ConsoleUtil.consoleOnly(textArea1, String.format(" File Descriptors: %d/%d", fileSystem.getOpenFileDescriptors(),
                fileSystem.getMaxFileDescriptors()));

        for (OSFileStore fs : fileSystem.getFileStores()) {
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            ConsoleUtil.consoleOnly(textArea1, String.format(
                    " %s (%s) [%s] %s of %s free (%.1f%%), %s of %s files free (%.1f%%) is %s "
                            + (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s")
                            + " and is mounted at %s",
                    fs.getName(), fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
                    FormatUtil.formatValue(fs.getFreeInodes(), ""), FormatUtil.formatValue(fs.getTotalInodes(), ""),
                    100d * fs.getFreeInodes() / fs.getTotalInodes(), fs.getVolume(), fs.getLogicalVolume(),
                    fs.getMount()));
        }
    }

    private void printNetworkInterfaces(List<NetworkIF> list) {
        StringBuilder sb = new StringBuilder("Network Interfaces:");
        if (list.isEmpty()) {
            sb.append(" Unknown");
        } else {
            for (NetworkIF net : list) {
                sb.append("\n ").append(net.toString());
            }
        }
        ConsoleUtil.consoleOnly(textArea1, sb.toString());
    }

    private void printNetworkParameters(NetworkParams networkParams) {
        ConsoleUtil.consoleOnly(textArea1, "Network parameters:\n " + networkParams.toString());
    }

    private void printInternetProtocolStats(InternetProtocolStats ip) {
        ConsoleUtil.consoleOnly(textArea1, "Internet Protocol statistics:");
        ConsoleUtil.consoleOnly(textArea1, " TCPv4: " + ip.getTCPv4Stats());
        ConsoleUtil.consoleOnly(textArea1, " TCPv6: " + ip.getTCPv6Stats());
        ConsoleUtil.consoleOnly(textArea1, " UDPv4: " + ip.getUDPv4Stats());
        ConsoleUtil.consoleOnly(textArea1, " UDPv6: " + ip.getUDPv6Stats());
    }

    private void printDisplays(List<Display> list) {
        ConsoleUtil.consoleOnly(textArea1, "Displays:");
        int i = 0;
        for (Display display : list) {
            ConsoleUtil.consoleOnly(textArea1, " Display " + i + ":");
            ConsoleUtil.consoleOnly(textArea1, String.valueOf(display));
            i++;
        }
    }

    private void printUsbDevices(List<UsbDevice> list) {
        ConsoleUtil.consoleOnly(textArea1, "USB Devices:");
        for (UsbDevice usbDevice : list) {
            ConsoleUtil.consoleOnly(textArea1, String.valueOf(usbDevice));
        }
    }

    private void printSoundCards(List<SoundCard> list) {
        ConsoleUtil.consoleOnly(textArea1, "Sound Cards:");
        for (SoundCard card : list) {
            ConsoleUtil.consoleOnly(textArea1, " " + String.valueOf(card));
        }
    }

    private void printGraphicsCards(List<GraphicsCard> list) {
        ConsoleUtil.consoleOnly(textArea1, "Graphics Cards:");
        if (list.isEmpty()) {
            ConsoleUtil.consoleOnly(textArea1, " None detected.");
        } else {
            for (GraphicsCard card : list) {
                ConsoleUtil.consoleOnly(textArea1, " " + String.valueOf(card));
            }
        }
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
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("Start");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel3.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textArea1 = new JTextArea();
        scrollPane1.setViewportView(textArea1);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
