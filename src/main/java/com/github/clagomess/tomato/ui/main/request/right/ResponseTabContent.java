package com.github.clagomess.tomato.ui.main.request.right;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.ui.component.RawTextArea;
import com.github.clagomess.tomato.ui.component.TRSyntaxTextArea;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

@Slf4j
@Getter
public class ResponseTabContent extends JPanel {
    private final RawTextArea txtHTTPDebug = new RawTextArea();
    private final TRSyntaxTextArea txtResponse = new TRSyntaxTextArea();
    private final StatusResponseUI statusResponseUI = new StatusResponseUI();
    private final JButton btnDownload = new JButton("Download");

    private ResponseDto responseDto = null;

    public ResponseTabContent(){
        setLayout(new MigLayout("insets 10 5 10 10", "[grow,fill]", ""));

        add(statusResponseUI, "width 100%");
        add(btnDownload, "wrap");

        JTabbedPane tpResponse = new JTabbedPane();
        tpResponse.addTab("Preview", TRSyntaxTextArea.createScroll(txtResponse));
        tpResponse.addTab("Header", getHeader());
        tpResponse.addTab("Debug", RawTextArea.createScroll(txtHTTPDebug));
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
        txtHTTPDebug.reset();
        txtResponse.reset();
        statusResponseUI.reset();
    }

    public void update(ResponseDto responseDto){
        this.responseDto = responseDto;
        txtHTTPDebug.setText(responseDto.getRequestDebug());

        if(responseDto.isRequestStatus()) {
            btnDownload.setEnabled(true);
            txtResponse.setSyntaxStyle(responseDto.getHttpResponse().getContentType());
            txtResponse.setText(responseDto.getHttpResponse().getBodyAsString());
        }

        statusResponseUI.update(responseDto);
    }

    private void btnDownloadAction(){
        JFileChooser file = new JFileChooser();
        file.setFileSelectionMode(JFileChooser.FILES_ONLY);

        /* @TODO: check
        if(file.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
            try {
                DataService.getInstance().writeFile(file.getSelectedFile(), responseDto.getHttpResponse().getBody());
            } catch (IOException e){
                DialogFactory.createDialogException(this, e);
            }
        }

         */
    }
}
