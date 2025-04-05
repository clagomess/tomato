package io.github.clagomess.tomato.ui.main.request.right.statusbadge;

import io.github.clagomess.tomato.ui.component.ColorConstant;

import javax.swing.*;

public class InfoBadge extends JPanel {
    public InfoBadge(String message) {
        var color = ColorConstant.GRAY_MATCH;

        setBackground(color.background());

        var label = new JLabel(message);
        label.setForeground(color.foreground());
        add(label);
    }
}
