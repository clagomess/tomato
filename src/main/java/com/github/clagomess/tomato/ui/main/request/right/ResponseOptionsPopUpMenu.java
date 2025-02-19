package com.github.clagomess.tomato.ui.main.request.right;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxDownloadIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsMagicWandIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ResponseOptionsPopUpMenu extends JPopupMenu {
    public ResponseOptionsPopUpMenu(
            ResponseTabContent parent
    ) {
        var mBeautify = new JMenuItem("Beautify Response", new BxsMagicWandIcon());
        mBeautify.addActionListener(e -> parent.btnBeautifyResponseAction());
        add(mBeautify);

        var mSaveResponse = new JMenuItem("Save Response", new BxDownloadIcon());
        mSaveResponse.addActionListener(e -> parent.btnSaveResponseAction());
        add(mSaveResponse);

        addSeparator();

        var mOpenResponseFile = new JMenuItem("Open Response File");
        mOpenResponseFile.addActionListener(e -> new WaitExecution(parent, () ->
                openResponseFile(parent.getResponseDto().getHttpResponse())
        ).execute());
        add(mOpenResponseFile);

        var mCopyResponseFilePath = new JMenuItem("Copy Response File Path");
        mCopyResponseFilePath.addActionListener(e -> new WaitExecution(parent, () ->
                copyResponseFilePath(parent.getResponseDto().getHttpResponse())
        ).execute());
        add(mCopyResponseFilePath);
    }

    private void openResponseFile(
            ResponseDto.Response httpResponse
    ) throws IOException {
        var file = new File(
                System.getProperty("java.io.tmpdir"),
                String.format(
                        "tomato-open-%s-%s",
                        System.currentTimeMillis(),
                        httpResponse.getBodyDownloadFileName()
                )
        );
        file.deleteOnExit();

        Files.copy(httpResponse.getBody().toPath(), file.toPath());

        Desktop.getDesktop().open(file);
    }

    private void copyResponseFilePath(
            ResponseDto.Response httpResponse
    ){
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(
                        httpResponse.getBody().getAbsolutePath()
                ), null);
    }
}
