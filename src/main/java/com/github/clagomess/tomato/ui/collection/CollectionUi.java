package com.github.clagomess.tomato.ui.collection;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class CollectionUi extends JPanel {
    public CollectionUi() {
        setLayout(new MigLayout("", "[grow, fill]", ""));

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
        root.add(getFilho());
        root.add(getFilho());
        root.add(getFilho());
        root.add(getFilho());

        JTree tree = new JTree(root);
        tree.setRootVisible(false);

        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(getEnv(), "wrap");
        add(scrollPane, "height 100%");
    }

    public DefaultMutableTreeNode getFilho(){
        DefaultMutableTreeNode filho = new DefaultMutableTreeNode("FOO - API");
        filho.add(new DefaultMutableTreeNode("/api/get-stuff"));
        filho.add(new DefaultMutableTreeNode("/api/post-stuff"));
        filho.add(new DefaultMutableTreeNode("/api/put-stuff"));
        return filho;
    }

    public Component getEnv(){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());

        JComboBox<String> envs = new JComboBox<>();
        envs.setForeground(Color.GREEN);
        envs.addItem("Desenvolvimento");
        envs.addItem("Produção");

        jPanel.add(envs);

        return jPanel;
    }
}
