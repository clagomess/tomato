package com.github.clagomess.tomato.factory;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
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

        return textArea;
    }

    public static RTextScrollPane createScroll(RSyntaxTextArea textArea){
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }
}
