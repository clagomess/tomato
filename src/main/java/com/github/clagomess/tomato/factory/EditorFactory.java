package com.github.clagomess.tomato.factory;

import org.apache.commons.lang.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class EditorFactory {
    private final Theme theme;
    private static final EditorFactory instance = new EditorFactory();

    private EditorFactory(){
        try {
            theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized EditorFactory getInstance(){
        return instance;
    }

    public RSyntaxTextArea createEditor(){
        RSyntaxTextArea textArea = new RSyntaxTextArea();
        theme.apply(textArea);

        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        textArea.setCodeFoldingEnabled(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setAntiAliasingEnabled(true);

        return textArea;
    }

    public static RTextScrollPane createScroll(RSyntaxTextArea textArea){
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }

    public static String createSyntaxStyleFromContentType(String contentType){
        if(StringUtils.isBlank(contentType)) return SyntaxConstants.SYNTAX_STYLE_NONE;
        if(contentType.contains(";")) contentType = contentType.split(";")[0];
        contentType = contentType.trim().toLowerCase();

        switch (contentType){
            case "application/json":
                return SyntaxConstants.SYNTAX_STYLE_JSON;
        }

        return contentType;
    }

    public static JTextArea createRawTextViewer(){
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(new Font(
                "Consolas",
                textArea.getFont().getStyle(),
                textArea.getFont().getSize()
        ));

        return textArea;
    }

    public static JScrollPane createScroll(JTextArea textArea){
        JScrollPane sp = new JScrollPane(textArea);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }
}
