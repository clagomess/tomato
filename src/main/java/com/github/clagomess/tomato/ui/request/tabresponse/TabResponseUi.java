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
    private final JTextArea txtResponseRAW = EditorFactory.createRawTextViewer();
    private final JTextArea txtHTTPDebug = EditorFactory.createRawTextViewer();
    private final RSyntaxTextArea txtPreview = EditorFactory.getInstance().createEditor();
    private final StatusResponseUI statusResponseUI = new StatusResponseUI();

    public TabResponseUi(){
        setLayout(new MigLayout("insets 10 5 10 10", "[grow,fill]", ""));

        add(statusResponseUI, "width 100%");
        add(new JButton("Download"), "wrap");

        JTabbedPane tpResponse = new JTabbedPane();
        tpResponse.addTab("Preview", EditorFactory.createScroll(txtPreview));
        tpResponse.addTab("RAW", EditorFactory.createScroll(txtResponseRAW));
        tpResponse.addTab("Header", getHeader());
        tpResponse.addTab("HTTP", EditorFactory.createScroll(txtHTTPDebug));
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
        txtHTTPDebug.setText(responseDto.getRequestDebug());

        if(responseDto.isRequestStatus()) {
            txtResponseRAW.setText(responseDto.getHttpResponse().getBody());
            txtPreview.setText(responseDto.getHttpResponse().getBody());

            String syntax = EditorFactory.createSyntaxStyleFromContentType(
                    responseDto.getHttpResponse().getContentType().toString()
            );
            txtPreview.setSyntaxEditingStyle(syntax);
        }

        statusResponseUI.update(responseDto);
    }
}
