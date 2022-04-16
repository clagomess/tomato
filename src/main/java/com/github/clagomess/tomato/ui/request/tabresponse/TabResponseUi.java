package com.github.clagomess.tomato.ui.request.tabresponse;

import com.github.clagomess.tomato.factory.EditorFactory;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

@Getter
public class TabResponseUi extends JPanel {
    private final JLabel lblHttpStatus = new JLabel("200 OK");
    private final JLabel lblResponseTime = new JLabel("3.2s");
    private final JLabel lblResponseSize = new JLabel("14KB");

    private final JTextArea txtResponseRAW = new JTextArea();
    private final JTextArea txtHTTPDebug = new JTextArea();

    public TabResponseUi(){
        setLayout(new MigLayout("insets 10 0 10 0", "[grow,fill]", ""));

        JPanel pResponseStatus = new JPanel();
        pResponseStatus.add(lblHttpStatus);
        pResponseStatus.add(lblResponseTime);
        pResponseStatus.add(lblResponseSize);
        add(pResponseStatus, "wrap, width 100%");

        JTabbedPane tpResponse = new JTabbedPane();
        tpResponse.addTab("Preview", EditorFactory.createScroll(EditorFactory.getInstance().createEditor()));
        tpResponse.addTab("RAW", txtResponseRAW);
        tpResponse.addTab("Header", getHeader());
        tpResponse.addTab("HTTP", txtHTTPDebug);
        add(tpResponse, "height 100%");
    }

    public Component getHeader(){
        DefaultTableModel tblCameraDTM = new DefaultTableModel();

        JTable table = new JTable(tblCameraDTM);
        table.setFocusable(false);
        table.setShowGrid(true);
        tblCameraDTM.addColumn("Header");
        tblCameraDTM.addColumn("Value");
        tblCameraDTM.addRow(new String[]{"Content-Type", "application/json"});
        tblCameraDTM.addRow(new String[]{"Origin", "localhost"});

        JScrollPane spane = new JScrollPane();
        spane.setViewportView(table);

        return spane;
    }
}
