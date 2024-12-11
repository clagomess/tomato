package com.github.clagomess.tomato.ui.component;

import jakarta.ws.rs.core.MediaType;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.io.IOException;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static jakarta.ws.rs.core.MediaType.TEXT_HTML_TYPE;

public class TRSyntaxTextArea extends RSyntaxTextArea {
    public TRSyntaxTextArea() {
        setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        setCodeFoldingEnabled(true);
        setLineWrap(true);
        setWrapStyleWord(true);
        setAntiAliasingEnabled(true);

        SwingUtilities.invokeLater(() -> {
            try {
                var theme = Theme.load(getClass().getResourceAsStream(
                        "/org/fife/ui/rsyntaxtextarea/themes/dark.xml"
                ));

                theme.apply(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
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

        if(contentType.isCompatible(APPLICATION_JSON_TYPE)) {
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

    public static RTextScrollPane createScroll(RSyntaxTextArea textArea){
        var sp = new RTextScrollPane(textArea);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }
}
