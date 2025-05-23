package io.github.clagomess.tomato.ui.main.request.right;

import io.github.clagomess.tomato.io.beautifier.Beautifier;
import io.github.clagomess.tomato.io.beautifier.JsonBeautifier;
import io.github.clagomess.tomato.io.beautifier.XmlBeautifier;
import io.github.clagomess.tomato.io.http.HttpService;
import io.github.clagomess.tomato.io.http.MediaType;
import io.github.clagomess.tomato.ui.component.ExceptionDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.concurrent.ForkJoinPool;

import static javax.swing.SwingUtilities.invokeLater;

public class BeautifierDialog extends JDialog {
    private final Component parent;
    private final MediaType contentType;
    private final Beautifier beautifier;

    private final JProgressBar progress = new JProgressBar();

    public BeautifierDialog(Component parent, MediaType contentType) {
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

    public void beautify(
            File inputFile,
            OnCompleteFI<File> onComplete
    ){
        if(beautifier == null) return;

        progress.setMaximum((int) inputFile.length());

        ForkJoinPool.commonPool().submit(() -> {
            try {
                var newResponseFile = HttpService.createTempFile();

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
            }catch (Exception e){
                invokeLater(() -> new ExceptionDialog(parent, e));
            }

            invokeLater(this::dispose);
        });

        invokeLater(() -> setVisible(true));
    }

    public void beautify(
            String inputString,
            OnCompleteFI<String> onComplete
    ){
        if(beautifier == null) return;

        progress.setMaximum(inputString.length());

        ForkJoinPool.commonPool().submit(() -> {
            try {
                var strWriter = new StringWriter();

                try (
                        var reader = new BufferedReader(new StringReader(inputString));
                        var writer = new BufferedWriter(strWriter)
                ) {
                    beautifier.setReader(reader);
                    beautifier.setWriter(writer);
                    beautifier.parse();
                }

                onComplete.run(strWriter.toString());
            }catch (Exception e){
                invokeLater(() -> new ExceptionDialog(parent, e));
            }

            invokeLater(this::dispose);
        });

        invokeLater(() -> setVisible(true));
    }

    @FunctionalInterface
    public interface OnCompleteFI<T> {
        void run(T result);
    }
}
