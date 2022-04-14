package com.github.clagomess.tomato.form;

import com.github.clagomess.tomato.form.collection.CollectionForm;
import com.github.clagomess.tomato.form.request.RequestForm;

import javax.swing.*;
import java.awt.*;

public class MainForm extends JFrame {
    private final CollectionForm collectionForm = new CollectionForm();
    private final RequestForm requestForm = new RequestForm();

    public MainForm(){
        setTitle("Tomato");
        setVisible(true);
        setMinimumSize(new Dimension(1000, 500));
        setJMenuBar(getMenu());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, collectionForm, requestForm);
        splitPane.setOneTouchExpandable(true);
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
