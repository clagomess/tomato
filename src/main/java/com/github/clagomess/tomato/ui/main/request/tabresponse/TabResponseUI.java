package com.github.clagomess.tomato.ui.main.request.tabresponse;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.factory.DialogFactory;
import com.github.clagomess.tomato.factory.EditorFactory;
import com.github.clagomess.tomato.service.DataService;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

@Getter
public class TabResponseUI extends JPanel {
    private final JTextArea txtResponseRAW = EditorFactory.createRawTextViewer();
    private final JTextArea txtHTTPDebug = EditorFactory.createRawTextViewer();
    private final RSyntaxTextArea txtPreview = EditorFactory.getInstance().createEditor();
    private final StatusResponseUI statusResponseUI = new StatusResponseUI();
    private final JButton btnDownload = new JButton("Download");

    private ResponseDto responseDto = null;

    public TabResponseUI(){
        setLayout(new MigLayout("insets 10 5 10 10", "[grow,fill]", ""));

        add(statusResponseUI, "width 100%");
        add(btnDownload, "wrap");

        JTabbedPane tpResponse = new JTabbedPane();
        tpResponse.addTab("Preview", EditorFactory.createScroll(txtPreview));
        tpResponse.addTab("RAW", EditorFactory.createScroll(txtResponseRAW));
        tpResponse.addTab("Header", getHeader());
        tpResponse.addTab("Debug", EditorFactory.createScroll(txtHTTPDebug));
        add(tpResponse, "span, height 100%");

        // configure
        btnDownload.addActionListener(l -> btnDownloadAction());
        btnDownload.setEnabled(false);
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

    public void reset(){
        btnDownload.setEnabled(false);
        txtResponseRAW.setText("");
        txtHTTPDebug.setText("");
        txtPreview.setText("");
        statusResponseUI.reset();
    }

    public void update(ResponseDto responseDto){
        this.responseDto = responseDto;
        txtHTTPDebug.setText(responseDto.getRequestDebug());

        if(responseDto.isRequestStatus()) {
            btnDownload.setEnabled(true);
            txtResponseRAW.setText(responseDto.getHttpResponse().getBodyAsString());
            txtPreview.setText(responseDto.getHttpResponse().getBodyAsString());

            String syntax = EditorFactory.createSyntaxStyleFromContentType(
                    responseDto.getHttpResponse().getContentType().toString()
            );
            txtPreview.setSyntaxEditingStyle(syntax);
        }

        statusResponseUI.update(responseDto);
    }

    private void btnDownloadAction(){
        JFileChooser file = new JFileChooser();
        file.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if(file.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
            try {
                DataService.getInstance().writeFile(file.getSelectedFile(), responseDto.getHttpResponse().getBody());
            } catch (IOException e){
                DialogFactory.createDialogException(this, e);
            }
        }
    }
}
