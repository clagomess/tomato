package com.github.clagomess.tomato.ui.request.tabresponse;

import com.github.clagomess.tomato.factory.EditorFactory;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

@Getter
public class TabResponseUi extends JPanel {
    private final JTextArea txtResponseRAW = new JTextArea();
    private final JTextArea txtHTTPDebug = new JTextArea();

    public TabResponseUi(){
        setLayout(new MigLayout("insets 10 5 10 10", "[grow,fill]", ""));

        add(new StatusResponseUI(200, 3.2, 14), "width 100%");
        add(new JButton("Download"), "wrap");

        JTabbedPane tpResponse = new JTabbedPane();
        tpResponse.addTab("Preview", EditorFactory.createScroll(EditorFactory.getInstance().createEditor()));
        tpResponse.addTab("RAW", txtResponseRAW);
        tpResponse.addTab("Header", getHeader());
        tpResponse.addTab("HTTP", txtHTTPDebug);
        add(tpResponse, "span, height 100%");
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
