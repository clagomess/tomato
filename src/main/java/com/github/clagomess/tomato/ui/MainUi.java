package com.github.clagomess.tomato.ui;

import com.github.clagomess.tomato.factory.IconFactory;
import com.github.clagomess.tomato.ui.main.collection.CollectionUI;
import com.github.clagomess.tomato.ui.main.request.RequestUI;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

@Getter
public class MainUi extends JFrame {
    private final CollectionUI collectionUi;
    private final RequestUI requestUi;

    public MainUi(){
        this.collectionUi = new CollectionUI();
        this.requestUi = new RequestUI();

        setTitle("Tomato");
        setIconImage(IconFactory.ICON_FAVICON.getImage());
        setVisible(true);
        setMinimumSize(new Dimension(1000, 500));
        setJMenuBar(getMenu());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, collectionUi, requestUi);
        splitPane.setDividerLocation(200);

        add(splitPane);
        pack();
    }

    private JMenuItem createMenuItem(String label, ActionListener action){
        JMenuItem menu = new JMenuItem(label);
        menu.addActionListener(action);
        return menu;
    }

    public JMenuBar getMenu(){
        JMenuBar menuBar = new JMenuBar();

        // workspace
        JMenu mWorkspace = new JMenu("Workspace");
        mWorkspace.add(createMenuItem("Switch", l -> {})); //@TODO: impl. menu workspace.switch
        mWorkspace.add(createMenuItem("Edit Workspaces", l -> {})); //@TODO: impl. menu workspace.switch
        menuBar.add(mWorkspace);

        // collection
        JMenu mCollection = new JMenu("Collection");
        mCollection.add(createMenuItem("Edit Collections", l -> {})); //@TODO: impl. menu Collection.switch
        mCollection.add(createMenuItem("Import", l -> {})); //@TODO: impl. menu Collection.switch
        mCollection.add(createMenuItem("Export", l -> {})); //@TODO: impl. menu Collection.switch
        menuBar.add(mCollection);

        // environment
        JMenu mEnviroment = new JMenu("Environment");
        mEnviroment.add(createMenuItem("Edit Environments", l -> {})); //@TODO: impl. menu Environment.switch
        mEnviroment.add(createMenuItem("Import", l -> {})); //@TODO: impl. menu Environment.switch
        mEnviroment.add(createMenuItem("Export", l -> {})); //@TODO: impl. menu Environment.switch
        menuBar.add(mEnviroment);

        // about
        JMenu mSettings = new JMenu("Settings");
        mSettings.addActionListener(l -> {}); //@TODO: impl. menu Settings
        menuBar.add(mSettings);

        // about
        JMenu mAbout = new JMenu("About");
        mAbout.addActionListener(l -> {}); //@TODO: impl. menu About
        menuBar.add(mAbout);

        return menuBar;
    }
}
