package com.github.clagomess.tomato.ui.component;

import javax.swing.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CharsetComboBox extends JComboBox<Charset> {
    public CharsetComboBox() {
        super(new Charset[]{
                StandardCharsets.UTF_8,
                StandardCharsets.ISO_8859_1,
                StandardCharsets.US_ASCII,
        });

        setSelectedItem(StandardCharsets.UTF_8);
    }

    @Override
    public Charset getSelectedItem() {
        return (Charset) super.getSelectedItem();
    }
}
