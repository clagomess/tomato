package com.github.clagomess.tomato.form;

import javax.swing.*;
import java.awt.*;

public class MainForm extends JFrame {
    public MainForm(){
        setTitle("Tomato");
        setVisible(true);
        setMinimumSize(new Dimension(1000, 500));
        setJMenuBar(getMenu());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JScrollPane spnaeA = new JScrollPane(); //@TODO: collections
        JScrollPane spnaeB = new JScrollPane(); //@TODO: abas

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spnaeA, spnaeB);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);

        add(splitPane);
        pack();
    }

    public JMenuBar getMenu(){
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new JMenu("Arquivo"));

        return menuBar;
    }
}
