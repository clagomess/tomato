package com.github.clagomess.tomato.ui.component;

import javax.swing.*;
import java.awt.*;

public class LoadingPane extends JPanel {
    public LoadingPane() {
        super(new FlowLayout(FlowLayout.CENTER));
        add(new JLabel("loading"));
    }
}
