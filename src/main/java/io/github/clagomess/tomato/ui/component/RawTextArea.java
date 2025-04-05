package io.github.clagomess.tomato.ui.component;

import javax.swing.*;
import javax.swing.border.MatteBorder;

public class RawTextArea extends JTextArea {
    public RawTextArea() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        putClientProperty("FlatLaf.style", "font: monospaced -1");
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
        sp.setBorder(new MatteBorder(0, 1, 1, 1, ColorConstant.GRAY));
        return sp;
    }
}
