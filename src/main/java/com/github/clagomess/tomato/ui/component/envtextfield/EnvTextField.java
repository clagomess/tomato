package com.github.clagomess.tomato.ui.component.envtextfield;

import javax.swing.*;

public class EnvTextField extends JScrollPane {
    private final EnvDocumentListener envDocumentListener;
    private final JTextPane textPane;

    public EnvTextField() {
        this.textPane = new JTextPane();

        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

        this.envDocumentListener = new EnvDocumentListener(this.textPane.getStyledDocument());
        this.textPane.getStyledDocument().addDocumentListener(this.envDocumentListener);

        setViewportView(this.textPane);
    }

    public void addOnChange(EnvTextFieldOnChangeFI value){
        envDocumentListener.getOnChangeList().add(value);
    }

    public void setText(String text){
        textPane.setText(text);
    }
}
