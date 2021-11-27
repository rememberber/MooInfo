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
import oshi.hardware.NetworkIF;
import oshi.software.os.NetworkParams;
import oshi.software.os.OperatingSystem;
import oshi.util.Constants;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * NetworkForm
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/12.
 */
@Getter
public class NetworkForm {
    private JPanel mainPanel;
    private JTable interfacesTable;
    private JPanel paramatersPanel;
    private JTextPane parametersTextPane;
    private JLabel uploadSpeedLabel;
    private JLabel downloadSpeedLabel;

    private static final int INIT_HASH_SIZE = 100;
    private static final String IP_ADDRESS_SEPARATOR = "; ";
    private static final String[] COLUMNS = {"Name", "Index", "Speed", "IPv4 Address", "IPv6 address", "MAC address"};
    private static final double[] COLUMN_WIDTH_PERCENT = {0.02, 0.02, 0.1, 0.25, 0.45, 0.15};

    private static final Log logger = LogFactory.get();

    private static NetworkForm networkForm;

    private static Long downloadBefore;
    private static Long uploadBefore;
    private static Long timestampBefore;

    public static NetworkForm getInstance() {
        if (networkForm == null) {
            networkForm = new NetworkForm();
        }
        return networkForm;
    }

    public static void init() {
        networkForm = getInstance();

        initUi();
        initInfo();
    }

    private static void initUi() {
        NetworkForm networkForm = getInstance();
        Style.emphaticIndicatorFont(networkForm.getUploadSpeedLabel());
        Style.emphaticIndicatorFont(networkForm.getDownloadSpeedLabel());
    }

    private static void initInfo() {
        initParameters();
        initInterfaces();

        ScheduledExecutorService serviceStartPerSecond = Executors.newSingleThreadScheduledExecutor();
        serviceStartPerSecond.scheduleAtFixedRate(NetworkForm::initNetworkSpeed, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Codes are copied from oshi and have some modifications.
     */
    private static void initInterfaces() {
        JTable interfacesTable = getInstance().getInterfacesTable();

        List<NetworkIF> networkIfList = App.si.getHardware().getNetworkIFs(true);

        TableModel model = new DefaultTableModel(parseInterfaces(networkIfList), COLUMNS);
        interfacesTable.setModel(model);
        resizeColumns(interfacesTable.getColumnModel());

        DefaultTableCellRenderer hr = (DefaultTableCellRenderer) interfacesTable.getTableHeader()
                .getDefaultRenderer();
        // The name of header column turn to left
        hr.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
        interfacesTable.setShowGrid(true);
    }

    private static void initParameters() {
        JTextPane parametersTextPane = networkForm.getParametersTextPane();
        parametersTextPane.setContentType("text/plain; charset=utf-8");

        String contentType = "text/html; charset=utf-8";
        parametersTextPane.setContentType(contentType);
        parametersTextPane.setText(buildParamsText(App.si.getOperatingSystem()));
    }

    private static void initNetworkSpeed() {

        String genericString;
        try {
            genericString = getDefaultNetworkInteface();
        } catch (Exception e) {
            logger.error("NetworkSpeed not supported");
            return;
        }

        long downloadNow = 0;
        long uploadNow = 0;
        long timestampNow = 0;

        List<NetworkIF> networkIFs = App.si.getHardware().getNetworkIFs();

//        int i = 0;
//        NetworkIF net = networkIFs.get(0);
//        try {
//            while (!networkIFs.get(i).getName().equals(genericString)) {
//                net = networkIFs.get(i);
//                i++;
//            }
//        } catch (ArrayIndexOutOfBoundsException e) {
//            logger.error("NetworkSpeed not supported");
//            return;
//        }
//        net.updateAttributes();

        for (int i = 0; i < networkIFs.size(); i++) {
            NetworkIF net = networkIFs.get(i);
            net.updateAttributes();
            downloadNow += net.getBytesRecv();
            uploadNow += net.getBytesSent();
            timestampNow = net.getTimeStamp();
        }

        if (downloadBefore == null) {
            downloadBefore = downloadNow;
        }
        if (uploadBefore == null) {
            uploadBefore = uploadNow;
        }
        if (timestampBefore == null) {
            timestampBefore = timestampNow - 1;
        }

        NetworkForm networkForm = getInstance();
        networkForm.getUploadSpeedLabel().setText("↓: " + DataSizeUtil.format((downloadNow - downloadBefore) / (timestampNow - timestampBefore) * 1000) + "/s");
        networkForm.getDownloadSpeedLabel().setText("↑: " + DataSizeUtil.format((uploadNow - uploadBefore) / (timestampNow - timestampBefore) * 1000) + "/s");

        downloadBefore = downloadNow;
        uploadBefore = uploadNow;
        timestampBefore = timestampNow;
    }

    private static String buildParamsText(OperatingSystem os) {
        NetworkParams networkParams = os.getNetworkParams();
        StringBuilder builder = new StringBuilder();
        builder.append("<br/>");
        builder.append("<b>Domain Name: </b>").append(networkParams.getDomainName());
        builder.append("<br/><b>Host Name: </b>").append(networkParams.getHostName());
        builder.append("<br/><b>Ipv4 Default Gateway: </b>").append(networkParams.getIpv4DefaultGateway());
        builder.append("<br/><b>Ipv6 Default Gateway: </b>").append(networkParams.getIpv6DefaultGateway());
        builder.append("<br/><b>Dns Servers: </b>").append(Arrays.toString(networkParams.getDnsServers()));
        builder.append("<br/>");
        builder.append("<br/>");
        return builder.toString();
    }

    /**
     * Codes are copied from oshi and have some modifications.
     *
     * @param ipAddressArr
     * @return
     */
    private static String getIPAddressesString(String[] ipAddressArr) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (String ipAddress : ipAddressArr) {
            if (first) {
                first = false;
            } else {
                sb.append(IP_ADDRESS_SEPARATOR);
            }
            sb.append(ipAddress);
        }

        return sb.toString();
    }

    /**
     * Codes are copied from oshi and have some modifications.
     *
     * @param list
     * @return
     */
    private static Object[][] parseInterfaces(List<NetworkIF> list) {
        Map<NetworkIF, Integer> intfSortValueMap = new HashMap<>(INIT_HASH_SIZE);
        for (NetworkIF intf : list) {
            intfSortValueMap.put(intf, intf.getIndex());
        }
        List<Map.Entry<NetworkIF, Integer>> intfList = new ArrayList<>(intfSortValueMap.entrySet());
        intfList.sort(Map.Entry.comparingByValue());

        int i = 0;
        Object[][] intfArr = new Object[intfList.size()][COLUMNS.length];

        for (Map.Entry<NetworkIF, Integer> e : intfList) {
            NetworkIF intf = e.getKey();

            intfArr[i][0] = intf.getName();
            intfArr[i][1] = intf.getIndex();
            intfArr[i][2] = intf.getSpeed();
            intfArr[i][3] = getIPAddressesString(intf.getIPv4addr());
            intfArr[i][4] = getIPAddressesString(intf.getIPv6addr());
            intfArr[i][5] = Constants.UNKNOWN.equals(intf.getMacaddr()) ? "" : intf.getMacaddr();

            i++;
        }

        return intfArr;
    }

    /**
     * Codes are copied from oshi and have some modifications.
     *
     * @param tableColumnModel
     */
    private static void resizeColumns(TableColumnModel tableColumnModel) {
        TableColumn column;
        int tW = tableColumnModel.getTotalColumnWidth();
        int cantCols = tableColumnModel.getColumnCount();
        for (int i = 0; i < cantCols; i++) {
            column = tableColumnModel.getColumn(i);
            int pWidth = (int) Math.round(COLUMN_WIDTH_PERCENT[i] * tW);
            column.setPreferredWidth(pWidth);
        }
    }

    /**
     * @return
     * @throws Exception
     */
    private static String getDefaultNetworkInteface() throws Exception {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        InetAddress localHost = InetAddress.getLocalHost();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress.equals(localHost)) {
                    return networkInterface.getName();
                }
            }
        }
        return "";
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(10, 20, 20, 20), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        paramatersPanel = new JPanel();
        paramatersPanel.setLayout(new GridLayoutManager(1, 1, new Insets(10, 0, 10, 0), -1, -1));
        panel1.add(paramatersPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        parametersTextPane = new JTextPane();
        parametersTextPane.setEditable(true);
        paramatersPanel.add(parametersTextPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        interfacesTable = new JTable();
        scrollPane1.setViewportView(interfacesTable);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(20, 10, 10, 10), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        uploadSpeedLabel = new JLabel();
        uploadSpeedLabel.setText(" ↑: --");
        panel3.add(uploadSpeedLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 10, 0, 0), -1, -1));
        panel2.add(panel4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        downloadSpeedLabel = new JLabel();
        downloadSpeedLabel.setText(" ↓: --");
        panel4.add(downloadSpeedLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
