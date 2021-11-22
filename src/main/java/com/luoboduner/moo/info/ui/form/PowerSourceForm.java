package com.luoboduner.moo.info.ui.form;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoboduner.moo.info.App;
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

    }

    private static void initInfo() {
        List<PowerSource> powerSources = App.si.getHardware().getPowerSources();
        PowerSourceForm powerSourceForm = getInstance();
        JPanel powerBasePanel = powerSourceForm.getPowerBasePanel();

        powerBasePanel.removeAll();

        powerBasePanel.setLayout(new GridLayoutManager(powerSources.size(), 1, new Insets(0, 0, 0, 0), -1, -1));

        StringBuilder powerSourceInfoTextBuilder = new StringBuilder();
        for (int i = 0; i < powerSources.size(); i++) {

            PowerSource powerSource = powerSources.get(i);

            powerSourceInfoTextBuilder.append(powerSource.toString());
            powerSourceInfoTextBuilder.append("\n");

            JPanel powerPanel = new JPanel();
            powerPanel.setLayout(new GridLayoutManager(3, 4, new Insets(10, 10, 10, 10), -1, -1));

            JLabel powerNameLabel = new JLabel();
            StringBuilder powerNameBuilder = new StringBuilder();
            powerNameBuilder.append(powerSource.getName());
            powerNameBuilder.append(" ").append(powerSource.getManufacturer());
            powerNameBuilder.append(" ").append(powerSource.getDeviceName());
            powerNameBuilder.append(" ").append(powerSource.getChemistry());
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
            capacityBuilder.append(" (").append(powerSource.getCapacityUnits()).append(")");
            capacityLabel.setText(capacityBuilder.toString());
            powerPanel.add(capacityLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            JLabel temperatureLabel = new JLabel();
            temperatureLabel.setText(String.valueOf(String.format("Temperature: %.1f°C", powerSource.getTemperature())));
            powerPanel.add(temperatureLabel, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            final Spacer spacer2 = new Spacer();
            powerPanel.add(spacer2, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

            JLabel label1 = new JLabel();
            if (powerSource.isCharging()) {
                label1.setHorizontalAlignment(SwingConstants.RIGHT);
                label1.setHorizontalTextPosition(SwingConstants.RIGHT);
                label1.setIcon(new FlatSVGIcon("icons/Charging.svg"));
                label1.setText("Charging");
            }

            powerPanel.add(label1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
            final Spacer spacer3 = new Spacer();
            powerPanel.add(spacer3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

            powerBasePanel.add(powerPanel, new GridConstraints(i, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        }

        // info textPane
        powerSourceForm.getPowerInfoTextPane().setText(powerSourceInfoTextBuilder.toString());

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
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        scrollPane1.setViewportView(panel1);
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        powerBasePanel = new JPanel();
        powerBasePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(powerBasePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        powerInfoPanel = new JPanel();
        powerInfoPanel.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(powerInfoPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        powerInfoTextPane = new JTextPane();
        powerInfoTextPane.setEditable(false);
        powerInfoPanel.add(powerInfoTextPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
