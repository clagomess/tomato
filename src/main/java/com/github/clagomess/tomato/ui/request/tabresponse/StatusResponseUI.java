package com.github.clagomess.tomato.ui.request.tabresponse;

import javax.swing.*;
import java.awt.*;

public class StatusResponseUI extends JPanel {
    public StatusResponseUI(int httpStatus, double requestTime, double requestSize){
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(createContainer(Color.GREEN, String.format("%s OK", httpStatus))); //@TODO: needs improvements
        add(createContainer(Color.GRAY, String.format("%ss", requestTime))); //@TODO: needs improvements
        add(createContainer(Color.GRAY, String.format("%sKB", requestSize))); //@TODO: needs improvements
    }

    private JPanel createContainer(Color color, String text){
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);

        JPanel panel = new JPanel();
        panel.setBackground(color);
        panel.add(label);

        return panel;
    }
}
