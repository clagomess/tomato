package com.github.clagomess.tomato.ui.component;

import javax.swing.*;
import java.awt.*;

public class IconButton extends JButton {
    public IconButton(Icon icon, String toolTipText) {
        super(icon);
        setToolTipText(toolTipText);
        setBorderPainted(false);
        setBackground(null);
        setFocusable(false);
        setMargin(new Insets(0,0,0,0));
    }

    public IconButton(
            Icon icon,
            Icon disabledIcon,
            String toolTipText
    ) {
        this(icon, toolTipText);
        setDisabledIcon(disabledIcon);
    }
}
