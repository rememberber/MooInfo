package com.luoboduner.moo.info.ui.form;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.ui.Style;
import com.luoboduner.moo.info.util.ScrollUtil;
import lombok.Getter;
import oshi.hardware.PowerSource;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * NetworkForm
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/21.
 */
@Getter
public class PowerSourceForm {
    private JPanel mainPanel;
    private JPanel powerBasePanel;
    private JPanel powerInfoPanel;
    private JTextPane powerInfoTextPane;
    private JScrollPane scrollPane;

    private static final Log logger = LogFactory.get();

    private static PowerSourceForm powerSourceForm;

    public static PowerSourceForm getInstance() {
        if (powerSourceForm == null) {
            powerSourceForm = new PowerSourceForm();
        }
        return powerSourceForm;
    }

    public static void init() {
        powerSourceForm = getInstance();

        initUi();
        ScheduledExecutorService serviceStartPerSecond = Executors.newSingleThreadScheduledExecutor();
        serviceStartPerSecond.scheduleAtFixedRate(PowerSourceForm::initInfo, 0, 10, TimeUnit.SECONDS);
    }

    private static void initUi() {
        ScrollUtil.smoothPane(getInstance().getScrollPane());
    }

    private static void initInfo() {
        List<PowerSource> powerSources = App.si.getHardware().getPowerSources();
        PowerSourceForm powerSourceForm = getInstance();
        JPanel powerBasePanel = powerSourceForm.getPowerBasePanel();

        powerBasePanel.removeAll();

        powerBasePanel.setLayout(new GridLayoutManager(powerSources.size(), 1, new Insets(0, 0, 0, 0), -1, -1));

        for (int i = 0; i < powerSources.size(); i++) {

            PowerSource powerSource = powerSources.get(i);

            JPanel powerPanel = new JPanel();
            powerPanel.setLayout(new GridLayoutManager(3, 4, new Insets(10, 10, 10, 10), -1, -1));

            JLabel powerNameLabel = new JLabel();
            Style.emphaticTitleFont(powerNameLabel);
            StringBuilder powerNameBuilder = new StringBuilder();
            powerNameBuilder.append(powerSource.getName());
            powerNameBuilder.append(" ").append(powerSource.getManufacturer());
            powerNameBuilder.append(" ").append(powerSource.getDeviceName());
            if (!"unknown".equals(powerSource.getChemistry())) {
                powerNameBuilder.append(" ").append(powerSource.getChemistry());
            }
            powerNameLabel.setText(powerNameBuilder.toString());
            powerPanel.add(powerNameLabel, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            JProgressBar progressBar1 = new JProgressBar();
            progressBar1.setMaximum(100);
            double remainingCapacityPercent = powerSource.getRemainingCapacityPercent();
            int remainingCapacityPercentInt = (int) (remainingCapacityPercent * 100);
            progressBar1.setValue(remainingCapacityPercentInt);
            progressBar1.setStringPainted(true);
            progressBar1.setString(remainingCapacityPercentInt + "%");
            Dimension d = new Dimension(-1, 100);
            progressBar1.setMinimumSize(d);
            powerPanel.add(progressBar1, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            JLabel capacityLabel = new JLabel();

            StringBuilder capacityBuilder = new StringBuilder();
            capacityBuilder.append("Current ").append(powerSource.getCurrentCapacity());
            capacityBuilder.append(" / ").append("Max ").append(powerSource.getMaxCapacity());
            capacityBuilder.append(" / ").append("Design ").append(powerSource.getDesignCapacity());
            capacityBuilder.append(" (").append(powerSource.getCapacityUnits()).append(") ");
            capacityBuilder.append((powerSource.getDesignCapacity() - powerSource.getMaxCapacity()) * 100 / powerSource.getDesignCapacity()).append("% wastage");
            capacityLabel.setText(capacityBuilder.toString());
            powerPanel.add(capacityLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            JLabel temperatureLabel = new JLabel();
            temperatureLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            temperatureLabel.setHorizontalTextPosition(SwingConstants.RIGHT);

            StringBuilder powerTextBuilder = new StringBuilder();
            if (powerSource.isCharging()) {
                temperatureLabel.setIcon(new FlatSVGIcon("icons/Charging.svg"));
                powerTextBuilder.append("Charging");
            } else {
                temperatureLabel.setIcon(new FlatSVGIcon("icons/indicator_light.svg", 10, 10));
                powerTextBuilder.append("Remaining Time: " + formatTimeRemaining(powerSource.getTimeRemainingEstimated()));
            }

            powerTextBuilder.append(String.format(" / %.1f°C", powerSource.getTemperature()));
            temperatureLabel.setText(powerTextBuilder.toString());
            powerPanel.add(temperatureLabel, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            final Spacer spacer2 = new Spacer();
            powerPanel.add(spacer2, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

            JLabel label1 = new JLabel();
            powerPanel.add(label1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
            final Spacer spacer3 = new Spacer();
            powerPanel.add(spacer3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

            powerBasePanel.add(powerPanel, new GridConstraints(i, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        }

        // info textPane
        powerSourceForm.getPowerInfoTextPane().setContentType("text/html; charset=utf-8");
        powerSourceForm.getPowerInfoTextPane().setText(getPowerInfoText(powerSources));

    }

    public static String getPowerInfoText(List<PowerSource> powerSources) {
        StringBuilder powerInfoBuilder = new StringBuilder();

        for (PowerSource powerSource : powerSources) {
            powerInfoBuilder.append("<br/>");
            powerInfoBuilder.append("<b>Name: </b>").append(powerSource.getName());
            powerInfoBuilder.append("<br/><b>Device Name: </b>").append(powerSource.getDeviceName());
            powerInfoBuilder.append("<br/><b>Remaining Capacity Percent: </b>").append(powerSource.getRemainingCapacityPercent() * 100).append("%");
            powerInfoBuilder.append("<br/><b>Time Remaining: </b>").append(formatTimeRemaining(powerSource.getTimeRemainingEstimated()));
            powerInfoBuilder.append("<br/><b>Time Remaining Instant: </b>").append(formatTimeRemaining(powerSource.getTimeRemainingInstant()));
            powerInfoBuilder.append("<br/><b>Power Usage Rate: </b>").append(powerSource.getPowerUsageRate());
            powerInfoBuilder.append("<br/><b>Voltage: </b>").append(powerSource.getVoltage());
            powerInfoBuilder.append("<br/><b>Amperage: </b>").append(powerSource.getAmperage());
            powerInfoBuilder.append("<br/><b>Temperature: </b>").append(String.format("%.1f°C", powerSource.getTemperature()));
            powerInfoBuilder.append("<br/><b>Power OnLine: </b>").append(powerSource.isPowerOnLine());
            powerInfoBuilder.append("<br/><b>Charging: </b>").append(powerSource.isCharging());
            powerInfoBuilder.append("<br/><b>Discharging: </b>").append(powerSource.isDischarging());
            powerInfoBuilder.append("<br/><b>Cycle Count: </b>").append(powerSource.getCycleCount());
            powerInfoBuilder.append("<br/><b>Chemistry: </b>").append(powerSource.getChemistry());
            powerInfoBuilder.append("<br/><b>Manufacturer: </b>").append(powerSource.getManufacturer());
            powerInfoBuilder.append("<br/><b>Manufacture Date: </b>").append(powerSource.getManufactureDate());
            powerInfoBuilder.append("<br/><b>Serial Number: </b>").append(powerSource.getSerialNumber());

            powerInfoBuilder.append("<br/");
        }

        return powerInfoBuilder.toString();
    }


    /**
     * copied from oshi
     * Estimated time remaining on power source, formatted as HH:mm
     *
     * @param timeInSeconds The time remaining, in seconds
     * @return formatted String of time remaining
     */
    private static String formatTimeRemaining(double timeInSeconds) {
        String formattedTimeRemaining;
        if (timeInSeconds < -1.5) {
            formattedTimeRemaining = "Charging";
        } else if (timeInSeconds < 0) {
            formattedTimeRemaining = "Unknown";
        } else {
            int hours = (int) (timeInSeconds / 3600);
            int minutes = (int) (timeInSeconds % 3600 / 60);
            formattedTimeRemaining = String.format("%d:%02d", hours, minutes);
        }
        return formattedTimeRemaining;
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
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane.setViewportView(panel1);
        powerBasePanel = new JPanel();
        powerBasePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(powerBasePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        powerInfoPanel = new JPanel();
        powerInfoPanel.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(powerInfoPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        powerInfoTextPane = new JTextPane();
        powerInfoTextPane.setEditable(true);
        powerInfoPanel.add(powerInfoTextPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
