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
import oshi.PlatformEnum;
import oshi.SystemInfo;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * DiskForm
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/15.
 */
@Getter
public class DiskForm {
    private JPanel mainPanel;
    private JPanel diskListPanel;
    private JScrollPane scrollPane;

    private static final Log logger = LogFactory.get();

    private static DiskForm diskForm;

    public static DiskForm getInstance() {
        if (diskForm == null) {
            diskForm = new DiskForm();
        }
        return diskForm;
    }

    public static void init() {
        diskForm = getInstance();

        initUi();

        ScheduledExecutorService serviceStartPerSecond = Executors.newSingleThreadScheduledExecutor();
        serviceStartPerSecond.scheduleAtFixedRate(DiskForm::initInfo, 0, 10, TimeUnit.SECONDS);
    }

    private static void initUi() {
        ScrollUtil.smoothPane(getInstance().getScrollPane());
    }

    private static void initInfo() {
        FileSystem fileSystem = App.si.getOperatingSystem().getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores();
        JPanel diskListPanel = getInstance().getDiskListPanel();

        diskListPanel.removeAll();

        diskListPanel.setLayout(new GridLayoutManager(fileStores.size() + 1, 1, new Insets(0, 10, 0, 10), -1, -1));

        for (int i = 0; i < fileStores.size(); i++) {
            OSFileStore store = fileStores.get(i);

            JPanel diskPanel = new JPanel();
            diskPanel.setLayout(new GridLayoutManager(3, 3, new Insets(10, 0, 10, 0), -1, -1));
            JLabel title = new JLabel();
            StringBuilder titleBuilder = new StringBuilder();
            titleBuilder.append(store.getName());
            if (SystemInfo.getCurrentPlatform().equals(PlatformEnum.WINDOWS)) {
                titleBuilder.append(" - ");
                titleBuilder.append(store.getLabel());
            }
            title.setText(titleBuilder.toString());
            Font font = new Font(diskListPanel.getFont().getName(), Font.BOLD, diskListPanel.getFont().getSize() + 2);
            title.setFont(font);

            diskPanel.add(title, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            JProgressBar spacePercent = new JProgressBar();
            diskPanel.add(spacePercent, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            long usable = store.getUsableSpace();
            long total = store.getTotalSpace();
            spacePercent.setMaximum(100);
            int usagePercent = (int) ((total - usable) * 100 / total);
            spacePercent.setValue(usagePercent);
            spacePercent.setToolTipText(usagePercent + "%");

            JLabel used = new JLabel();
            used.setText("Used " + DataSizeUtil.format(total - usable) + "/" + DataSizeUtil.format(total));
            diskPanel.add(used, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            JLabel available = new JLabel();
            available.setText(DataSizeUtil.format(usable) + "/" + DataSizeUtil.format(total) + " Available");
            diskPanel.add(available, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
            final Spacer spacer1 = new Spacer();
            diskPanel.add(spacer1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

            diskListPanel.add(diskPanel, new GridConstraints(i, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        }

        final Spacer spacer1 = new Spacer();
        diskListPanel.add(spacer1, new GridConstraints(fileStores.size(), 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollPane = new JScrollPane();
        panel1.add(scrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        diskListPanel = new JPanel();
        diskListPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane.setViewportView(diskListPanel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
