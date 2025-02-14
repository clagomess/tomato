package com.github.clagomess.tomato.ui.main.request.right;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.table.KeyValueTMDto;
import com.github.clagomess.tomato.io.http.MediaType;
import com.github.clagomess.tomato.ui.component.ColorConstant;
import com.github.clagomess.tomato.ui.component.RawTextArea;
import com.github.clagomess.tomato.ui.component.TRSyntaxTextArea;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxDownloadIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsMagicWandIcon;
import com.github.clagomess.tomato.ui.component.tablemanager.TableManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.io.File;
import java.nio.file.Files;

import static javax.swing.SwingUtilities.invokeLater;

@Slf4j
@Getter
public class ResponseTabContent extends JPanel {
    private final StatusResponse statusResponse = new StatusResponse();

    private final JButton btnBeautify = new JButton(new BxsMagicWandIcon()){{
        setToolTipText("Beautify response");
        setEnabled(false);
    }};
    private final JButton btnDownload = new JButton(new BxDownloadIcon()){{
        setToolTipText("Download");
        setEnabled(false);
    }};

    private TRSyntaxTextArea txtResponse;
    private TableManager<KeyValueTMDto> tblResponseHeader;
    private RawTextArea txtHTTPDebug;

    private ResponseDto responseDto = null;

    public ResponseTabContent(){
        setLayout(new MigLayout(
                "insets 5 5 2 2",
                "[grow,fill][][]"
        ));

        add(statusResponse, "width 100%");
        add(btnBeautify);
        add(btnDownload, "wrap");

        JTabbedPane tpResponse = new JTabbedPane();
        add(tpResponse, "span, height 100%");

        invokeLater(() -> createTabReponse(tpResponse));
        invokeLater(() -> createTabHeader(tpResponse));
        // @TODO: add cookie viewer
        invokeLater(() -> createTabDebug(tpResponse));

        // configure
        btnBeautify.addActionListener(l -> btnBeautifyAction());
        btnDownload.addActionListener(l -> btnDownloadAction());
    }

    private void createTabReponse(JTabbedPane tabbedPane){
        txtResponse = new TRSyntaxTextArea();
        var component = TRSyntaxTextArea.createScroll(txtResponse);
        tabbedPane.addTab("Response", component);
    }

    private void createTabHeader(JTabbedPane tabbedPane){
        tblResponseHeader = new TableManager<>(KeyValueTMDto.class);

        var component = new JScrollPane(tblResponseHeader.getTable());
        component.setBorder(new MatteBorder(0, 1, 1, 1, ColorConstant.GRAY));
        tabbedPane.addTab("Header", component);
    }

    private void createTabDebug(JTabbedPane tabbedPane){
        txtHTTPDebug = new RawTextArea();
        var component =  RawTextArea.createScroll(txtHTTPDebug);
        tabbedPane.addTab("Debug", component);
    }

    public void reset(){
        btnBeautify.setSelected(false);
        btnDownload.setEnabled(false);
        txtHTTPDebug.reset();
        txtResponse.reset();
        statusResponse.reset();
    }

    public void update(@NotNull ResponseDto responseDto){
        this.responseDto = responseDto;
        txtHTTPDebug.setText(responseDto.getRequestDebug());

        if(responseDto.isRequestStatus()) {
            btnDownload.setEnabled(true);
            btnBeautify.setEnabled(true);

            txtResponse.setSyntaxStyle(
                    responseDto.getHttpResponse().isRenderBodyByContentType() ?
                    responseDto.getHttpResponse().getContentType() :
                    MediaType.TEXT_PLAIN
            );

            txtResponse.setText(responseDto.getHttpResponse().getBodyAsString());

            tblResponseHeader.getModel().clear();
            responseDto.getHttpResponse().getHeaders().forEach((key, value) -> {
                value.forEach(item -> {
                    tblResponseHeader.getModel().addRow(new KeyValueTMDto(
                            key,
                            item
                    ));
                });
            });
        }

        statusResponse.update(responseDto);
    }

    private void btnBeautifyAction(){
        new BeautifierDialog(this, responseDto.getHttpResponse().getContentType())
                .beautify(responseDto.getHttpResponse().getBody(), result -> {
                    responseDto.getHttpResponse().setBody(result);

                    SwingUtilities.invokeLater(() -> {
                        txtResponse.setText(responseDto.getHttpResponse().getBodyAsString());
                        txtResponse.setCaretPosition(0);
                        statusResponse.update(responseDto);
                    });
                });
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
