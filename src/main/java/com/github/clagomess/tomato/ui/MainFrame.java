package com.github.clagomess.tomato.ui;

import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import com.github.clagomess.tomato.ui.main.collection.CollectionTree;
import com.github.clagomess.tomato.ui.main.request.RequestTabbedPane;
import com.github.clagomess.tomato.ui.menu.CollectionMenu;
import com.github.clagomess.tomato.ui.menu.EnvironmentMenu;
import com.github.clagomess.tomato.ui.menu.SettingsMenu;
import com.github.clagomess.tomato.ui.menu.WorkspaceMenu;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;

public class MainFrame extends JFrame {
    public MainFrame(){
        setTitle("Tomato");
        setIconImages(FaviconImage.getFrameIconImage());
        setVisible(true);
        setMinimumSize(new Dimension(1200, 650));
        setJMenuBar(getMenu());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new MigLayout("insets 5", "[grow, fill]"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        add(splitPane, "height 100%");

        invokeLater(() -> {
            splitPane.setLeftComponent(new CollectionTree());
            splitPane.setDividerLocation(250);
            splitPane.revalidate();
            splitPane.repaint();
        });
        invokeLater(() -> {
            splitPane.setRightComponent(new RequestTabbedPane());
            splitPane.setDividerLocation(250);
            splitPane.revalidate();
            splitPane.repaint();
        });

        pack();
        setVisible(true);
    }

    public JMenuBar getMenu(){
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new WorkspaceMenu(this));
        menuBar.add(new CollectionMenu(this));
        menuBar.add(new EnvironmentMenu(this));
        menuBar.add(new SettingsMenu(this));

        return menuBar;
    }
}
