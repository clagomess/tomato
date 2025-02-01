package com.github.clagomess.tomato.ui.main.request.right;

import com.github.clagomess.tomato.io.beautifier.Beautifier;
import com.github.clagomess.tomato.io.beautifier.JsonBeautifier;
import com.github.clagomess.tomato.io.http.MediaType;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.*;

import static javax.swing.SwingUtilities.invokeLater;

public class BeautifierUI extends JDialog {
    public BeautifierUI(ResponseTabContent parent) {
        super(
                SwingUtilities.getWindowAncestor(parent),
                "Beautifier",
                Dialog.DEFAULT_MODALITY_TYPE
        );

        MediaType contentType = parent.getResponseDto()
                .getHttpResponse()
                .getContentType();

        Beautifier beautifier = getBeautifier(contentType);
        if(beautifier == null) {
            new ExceptionDialog(parent, String.format(
                    "%s is not supported",
                    contentType
            ));
            return;
        }

        setMinimumSize(new Dimension(350, 100));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLayout(new MigLayout(
                "",
                "[grow, fill]"
        ));

        long bodySize = parent.getResponseDto()
                .getHttpResponse()
                .getBodySize();

        add(new JLabel(String.format(
                "Processing %s bytes:",
                bodySize
        )), "wrap");

        JProgressBar progress = new JProgressBar();
        progress.setStringPainted(true);
        progress.setMinimum(0);
        progress.setMaximum((int) bodySize);
        beautifier.setProgress(value -> {
            invokeLater(() -> progress.setValue(value));
        });

        add(progress);

        pack();
        setLocationRelativeTo(parent);

        new Thread(() -> {
            try {
                var newResponseFile = File.createTempFile("tomato-response-", ".bin");
                newResponseFile.deleteOnExit();

                try(
                    var reader = new BufferedReader(new FileReader(parent.getResponseDto().getHttpResponse().getBody()));
                    var writer = new BufferedWriter(new FileWriter(newResponseFile))
                ){
                    beautifier.setReader(reader);
                    beautifier.setWriter(writer);
                    beautifier.parse();
                }

                parent.getResponseDto().getHttpResponse().setBody(newResponseFile);
                invokeLater(() -> {
                    parent.getBtnBeautify().setEnabled(false);
                    parent.refreshResponseContent();
                });
            }catch (Throwable e) {
                invokeLater(() -> new ExceptionDialog(parent, e));
            }

            invokeLater(this::dispose);
        }, "BeautifierUI").start();

        setVisible(true);
    }

    protected Beautifier getBeautifier(MediaType mediaType) {
        if (mediaType.isCompatible(MediaType.APPLICATION_JSON)) {
            return new JsonBeautifier();
        }

        // @TODO: implements XML beautifier

        return null;
    }
}
