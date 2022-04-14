package com.github.clagomess.tomato.ui.collection;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class CollectionUi extends JPanel {
    public CollectionUi() {
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(0, 0));

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
        root.add(getFilho());
        root.add(getFilho());
        root.add(getFilho());
        root.add(getFilho());

        JTree tree = new JTree(root);
        tree.setRootVisible(false);

        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane);
    }

    public DefaultMutableTreeNode getFilho(){
        DefaultMutableTreeNode filho = new DefaultMutableTreeNode("FOO - API");
        filho.add(new DefaultMutableTreeNode("/api/get-stuff"));
        filho.add(new DefaultMutableTreeNode("/api/post-stuff"));
        filho.add(new DefaultMutableTreeNode("/api/put-stuff"));
        return filho;
    }
}
