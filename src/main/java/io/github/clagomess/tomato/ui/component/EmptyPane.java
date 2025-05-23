package io.github.clagomess.tomato.ui.component;

import javax.swing.*;
import java.awt.*;

public class EmptyPane extends JPanel {
    public EmptyPane() {
        super(new FlowLayout(FlowLayout.CENTER));
        add(new JLabel("empty!"));
    }
}
