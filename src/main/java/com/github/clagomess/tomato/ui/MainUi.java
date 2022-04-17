package com.github.clagomess.tomato.ui;

import com.github.clagomess.tomato.ui.collection.CollectionUi;
import com.github.clagomess.tomato.ui.request.RequestUi;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class MainUi extends JFrame {
    private final CollectionUi collectionUi;
    private final RequestUi requestUi;

    public MainUi(){
        this.collectionUi = new CollectionUi();
        this.requestUi = new RequestUi();

        setTitle("Tomato");
        setVisible(true);
        setMinimumSize(new Dimension(1000, 500));
        setJMenuBar(getMenu());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, collectionUi, requestUi);
        splitPane.setDividerLocation(200);

        add(splitPane);
        pack();
    }

    public JMenuBar getMenu(){
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new JMenu("File"));
        menuBar.add(new JMenu("Workspace"));
        menuBar.add(new JMenu("Collection"));
        menuBar.add(new JMenu("Environment"));

        return menuBar;
    }
}
