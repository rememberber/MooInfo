package com.luoboduner.moo.info.ui.form;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.luoboduner.moo.info.App;
import lombok.Getter;
import oshi.hardware.NetworkIF;
import oshi.software.os.NetworkParams;
import oshi.software.os.OperatingSystem;
import oshi.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OverviewForm
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

    private static final int INIT_HASH_SIZE = 100;
    private static final String IP_ADDRESS_SEPARATOR = "; ";
    private static final String[] COLUMNS = {"Name", "Index", "Speed", "IPv4 Address", "IPv6 address", "MAC address"};
    private static final double[] COLUMN_WIDTH_PERCENT = {0.02, 0.02, 0.1, 0.25, 0.45, 0.15};

    private static final Log logger = LogFactory.get();

    private static NetworkForm networkForm;

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
    }

    private static void initInfo() {
        initParameters();
        initInterfaces();
    }

    private static void initInterfaces() {
        JTable interfacesTable = getInstance().getInterfacesTable();

        List<NetworkIF> networkIfList = App.si.getHardware().getNetworkIFs(true);

        TableModel model = new DefaultTableModel(parseInterfaces(networkIfList), COLUMNS);
        interfacesTable.setModel(model);
        resizeColumns(interfacesTable.getColumnModel());
        interfacesTable.setShowGrid(true);
    }

    private static void initParameters() {
        JTextPane parametersTextPane = networkForm.getParametersTextPane();
        parametersTextPane.setContentType("text/plain; charset=utf-8");
        parametersTextPane.setEditable(false);

        parametersTextPane.setText(buildParamsText(App.si.getOperatingSystem()));
    }

    private static String buildParamsText(OperatingSystem os) {
        NetworkParams params = os.getNetworkParams();
        StringBuilder sb = new StringBuilder("Host Name: ").append(params.getHostName());
        if (!params.getDomainName().isEmpty()) {
            sb.append("\nDomain Name: ").append(params.getDomainName());
        }
        sb.append("\nIPv4 Default Gateway: ").append(params.getIpv4DefaultGateway());
        if (!params.getIpv6DefaultGateway().isEmpty()) {
            sb.append("\nIPv6 Default Gateway: ").append(params.getIpv6DefaultGateway());
        }
        sb.append("\nDNS Servers: ").append(getIPAddressesString(params.getDnsServers()));
        return sb.toString();
    }

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
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        paramatersPanel = new JPanel();
        paramatersPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(paramatersPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        parametersTextPane = new JTextPane();
        paramatersPanel.add(parametersTextPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        interfacesTable = new JTable();
        scrollPane1.setViewportView(interfacesTable);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
