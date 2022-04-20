package com.github.clagomess.tomato.ui.request.tabresponse;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.factory.EditorFactory;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

@Getter
public class TabResponseUi extends JPanel {
    private final JTextArea txtResponseRAW = new JTextArea();
    private final JTextArea txtHTTPDebug = new JTextArea();
    private final RSyntaxTextArea txtPreview = EditorFactory.getInstance().createEditor();
    private final StatusResponseUI statusResponseUI = new StatusResponseUI();

    public TabResponseUi(){
        // @TODO: criar factory, e adicionar font consolas, adicionar Scroll
        txtHTTPDebug.setLineWrap(true);
        txtHTTPDebug.setWrapStyleWord(true);
        txtResponseRAW.setLineWrap(true);
        txtResponseRAW.setWrapStyleWord(true);

        setLayout(new MigLayout("insets 10 5 10 10", "[grow,fill]", ""));

        add(statusResponseUI, "width 100%");
        add(new JButton("Download"), "wrap");

        JTabbedPane tpResponse = new JTabbedPane();
        tpResponse.addTab("Preview", EditorFactory.createScroll(txtPreview));
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

    public void update(ResponseDto responseDto){
        txtResponseRAW.setText(responseDto.getHttpResponse().getBody());
        txtHTTPDebug.setText(responseDto.getRequestDebug());
        txtPreview.setText(responseDto.getHttpResponse().getBody());
        // @TODO: check when "text/html; charset=ISO-8859-1"
        txtPreview.setSyntaxEditingStyle(responseDto.getHttpResponse().getContentType().toString());
        statusResponseUI.update(responseDto);
    }
}
