package com.github.clagomess.tomato.ui.main.request.right.statusbadge;

import com.github.clagomess.tomato.ui.component.ColorConstant;

import javax.swing.*;

public class ErrorBadge extends JPanel {
    public ErrorBadge(String message) {
        var color = ColorConstant.RED_MATCH;

        setBackground(color.background());

        var label = new JLabel(message);
        label.setForeground(color.foreground());
        add(label);
    }
}
