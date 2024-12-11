package com.github.clagomess.tomato.ui.component;

import javax.swing.*;
import java.awt.*;

public class RawTextArea extends JTextArea {
    public RawTextArea() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        setFont(new Font(
                "Consolas",
                this.getFont().getStyle(),
                this.getFont().getSize()
        ));
    }

    public void reset(){
        try {
            getDocument().remove(0, getDocument().getLength());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JScrollPane createScroll(JTextArea textArea){
        var sp = new JScrollPane(textArea);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }
}
