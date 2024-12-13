package com.github.clagomess.tomato.ui.component;

import jakarta.ws.rs.core.MediaType;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.*;

public class TRSyntaxTextArea extends RSyntaxTextArea {
    private final List<ListenableTextField.OnChangeFI> onChangeList = new LinkedList<>();

    public TRSyntaxTextArea() {
        setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        setCodeFoldingEnabled(true);
        setLineWrap(true);
        setWrapStyleWord(true);
        setAntiAliasingEnabled(true);

        SwingUtilities.invokeLater(() -> {
            try {
                var theme = Theme.load(getClass().getResourceAsStream(
                        "trsyntax-textarea-theme.xml"
                ));

                theme.apply(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            public void update(){
                onChangeList.forEach(ch -> ch.change(getText()));
            }
        });
    }

    public void setSyntaxStyle(MediaType contentType){
        if(contentType.isCompatible(APPLICATION_JSON_TYPE)) {
            setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
            return;
        }

        if(contentType.isCompatible(TEXT_HTML_TYPE)) {
            setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
            return;
        }

        if(contentType.isCompatible(APPLICATION_XML_TYPE)) {
            setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
            return;
        }

        setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
    }

    public void reset(){
        try {
            getDocument().remove(0, getDocument().getLength());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addOnChange(ListenableTextField.OnChangeFI value){
        onChangeList.add(value);
    }

    @FunctionalInterface
    public interface OnChangeFI {
        void change(String content);
    }

    public static RTextScrollPane createScroll(RSyntaxTextArea textArea){
        var sp = new RTextScrollPane(textArea);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }
}
