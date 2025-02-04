package com.github.clagomess.tomato.ui.main.request.right;

import com.github.clagomess.tomato.io.beautifier.Beautifier;
import com.github.clagomess.tomato.io.beautifier.JsonBeautifier;
import com.github.clagomess.tomato.io.beautifier.XmlBeautifier;
import com.github.clagomess.tomato.io.http.MediaType;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.*;

import static javax.swing.SwingUtilities.invokeLater;

public class BeautifierUI extends JDialog {
    private final JComponent parent;
    private final MediaType contentType;
    private final Beautifier beautifier;

    private final JProgressBar progress = new JProgressBar();

    public BeautifierUI(JComponent parent, MediaType contentType) {
        super(
                SwingUtilities.getWindowAncestor(parent),
                "Beautifier",
                Dialog.DEFAULT_MODALITY_TYPE
        );

        this.parent = parent;
        this.contentType = contentType;

        beautifier = getBeautifier(contentType);
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

        add(new JLabel("Processing: "), "wrap");

        progress.setStringPainted(true);
        progress.setMinimum(0);
        beautifier.setProgress(value -> invokeLater(() -> {
            if (value == -1) {
                progress.setIndeterminate(true);
            } else {
                progress.setValue(value);
            }
        }));

        add(progress);

        pack();
        setLocationRelativeTo(parent);
    }

    protected Beautifier getBeautifier(MediaType mediaType) {
        if (mediaType.isCompatible(MediaType.APPLICATION_JSON)) {
            return new JsonBeautifier();
        }

        if(mediaType.isCompatible(MediaType.TEXT_XML) ||
                mediaType.isCompatible(MediaType.APPLICATION_XML)) {
            return new XmlBeautifier();
        }

        return null;
    }

    public BeautifierUI beautify(
            File inputFile,
            OnCompleteFI<File> onComplete
    ){
        if(beautifier == null) return this;

        progress.setMaximum((int) inputFile.length());

        new Thread(() -> {
            try {
                var newResponseFile = File.createTempFile("tomato-response-", ".bin");
                newResponseFile.deleteOnExit();

                try (
                        var reader = new BufferedReader(new FileReader(
                                inputFile,
                                contentType.getCharsetOrDefault()
                        ));
                        var writer = new BufferedWriter(new FileWriter(
                                newResponseFile,
                                contentType.getCharsetOrDefault()
                        ))
                ) {
                    beautifier.setReader(reader);
                    beautifier.setWriter(writer);
                    beautifier.parse();
                }

                onComplete.run(newResponseFile);
            }catch (Throwable e){
                invokeLater(() -> new ExceptionDialog(parent, e));
            }

            invokeLater(this::dispose);
        }, "BeautifierUI").start();

        return this;
    }

    @FunctionalInterface
    public interface OnCompleteFI<T> {
        void run(T result);
    }
}
