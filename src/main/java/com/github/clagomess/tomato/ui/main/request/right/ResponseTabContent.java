package com.github.clagomess.tomato.ui.main.request.right;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.table.ResponseHeaderTMDto;
import com.github.clagomess.tomato.io.http.MediaType;
import com.github.clagomess.tomato.ui.component.RawTextArea;
import com.github.clagomess.tomato.ui.component.TRSyntaxTextArea;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxDownloadIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsMagicWandIcon;
import com.github.clagomess.tomato.ui.component.tablemanager.TableManagerUI;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

@Slf4j
@Getter
public class ResponseTabContent extends JPanel {
    private final RawTextArea txtHTTPDebug = new RawTextArea();
    private final TRSyntaxTextArea txtResponse = new TRSyntaxTextArea();
    private final StatusResponseUI statusResponseUI = new StatusResponseUI();
    private final TableManagerUI<ResponseHeaderTMDto> tblResponseHeader = new TableManagerUI<>(
            ResponseHeaderTMDto.class
    );
    private final JButton btnBeautify = new JButton(new BxsMagicWandIcon()){{
        setToolTipText("Beautify response");
        setEnabled(false);
    }};
    private final JButton btnDownload = new JButton(new BxDownloadIcon()){{
        setToolTipText("Download");
    }};

    private ResponseDto responseDto = null;

    public ResponseTabContent(){
        setLayout(new MigLayout(
                "insets 5 5 0 0",
                "[grow,fill][][]",
                ""
        ));

        add(statusResponseUI, "width 100%");
        add(btnBeautify);
        add(btnDownload, "wrap");

        var hsp = new JScrollPane(tblResponseHeader.getTable());
        hsp.setBorder(new MatteBorder(0, 1, 1, 1, Color.decode("#616365")));

        JTabbedPane tpResponse = new JTabbedPane();
        tpResponse.addTab("Response", TRSyntaxTextArea.createScroll(txtResponse));
        tpResponse.addTab("Header", hsp);
        tpResponse.addTab("Debug", RawTextArea.createScroll(txtHTTPDebug));
        add(tpResponse, "span, height 100%");

        // configure
        // @TODO: impl. btnBeautify
        btnDownload.addActionListener(l -> btnDownloadAction());
        btnDownload.setEnabled(false);
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

            txtResponse.setSyntaxStyle(
                    responseDto.getHttpResponse().isRenderBodyByContentType() ?
                    responseDto.getHttpResponse().getContentType() :
                    MediaType.TEXT_PLAIN
            );

            txtResponse.setText(responseDto.getHttpResponse().getBodyAsString());

            tblResponseHeader.getModel().clear();
            responseDto.getHttpResponse().getHeaders()
                    .forEach((key, value) -> {
                        tblResponseHeader.getModel().addRow(new ResponseHeaderTMDto(
                                key,
                                String.join(",", value)
                        ));
                    });
        }

        statusResponseUI.update(responseDto);
    }

    private void btnDownloadAction(){
        JFileChooser file = new JFileChooser();
        file.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if(file.showSaveDialog(this) != JFileChooser.APPROVE_OPTION){
            return;
        }

        new WaitExecution(this, btnDownload, () -> {
            File bodyFile = responseDto.getHttpResponse().getBody();
            Files.copy(bodyFile.toPath(), file.getSelectedFile().toPath());
        }).execute();
    }
}
