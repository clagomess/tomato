package com.github.clagomess.tomato.ui.main.request.right;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.table.KeyValueTMDto;
import com.github.clagomess.tomato.io.http.MediaType;
import com.github.clagomess.tomato.ui.component.*;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxDotsVerticalRoundedIcon;
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

    private final JButton btnBeautifyResponse = new IconButton(new BxsMagicWandIcon(), "Beautify Response");
    private final JButton btnSaveResponse = new IconButton(new BxDownloadIcon(), "Save Response");
    private final JButton btnMoreOptions = new IconButton(new BxDotsVerticalRoundedIcon(), "More Options");

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
        add(btnBeautifyResponse);
        add(btnSaveResponse);
        add(btnMoreOptions, "wrap");

        JTabbedPane tpResponse = new JTabbedPane();
        add(tpResponse, "span, height 100%");

        invokeLater(() -> createTabReponse(tpResponse));
        invokeLater(() -> createTabHeader(tpResponse));
        // @TODO: add cookie viewer
        invokeLater(() -> createTabDebug(tpResponse));

        // configure
        setButtonsEnabled(false);
        btnBeautifyResponse.addActionListener(l -> btnBeautifyResponseAction());
        btnSaveResponse.addActionListener(l -> btnSaveResponseAction());
        btnMoreOptions.addActionListener(l ->
                new ResponseOptionsPopUpMenu(this)
                        .show(btnMoreOptions, 0, 0)
        );
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

    protected void setButtonsEnabled(boolean enabled){
        btnBeautifyResponse.setEnabled(enabled);
        btnSaveResponse.setEnabled(enabled);
        btnMoreOptions.setEnabled(enabled);
    }

    public void reset(){
        setButtonsEnabled(false);
        txtHTTPDebug.reset();
        txtResponse.reset();
        statusResponse.reset();
    }

    public void update(@NotNull ResponseDto responseDto){
        this.responseDto = responseDto;
        txtHTTPDebug.setText(responseDto.getRequestDebug());

        if(responseDto.isRequestStatus()) {
            setButtonsEnabled(true);

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

    protected void btnBeautifyResponseAction(){
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

    protected void btnSaveResponseAction(){
        JFileChooser file = new JFileChooser();
        file.setFileSelectionMode(JFileChooser.FILES_ONLY);
        file.setSelectedFile(new File(responseDto.getHttpResponse().getBodyDownloadFileName()));

        if(file.showSaveDialog(this) != JFileChooser.APPROVE_OPTION){
            return;
        }

        new WaitExecution(this, btnSaveResponse, () -> {
            File bodyFile = responseDto.getHttpResponse().getBody();
            Files.copy(bodyFile.toPath(), file.getSelectedFile().toPath());

            JOptionPane.showMessageDialog(
                    this,
                    "File Saved Successfully"
            );
        }).execute();
    }
}
