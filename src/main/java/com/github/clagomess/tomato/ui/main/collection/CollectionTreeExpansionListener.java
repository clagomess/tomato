package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.ui.main.collection.node.CollectionTreeNode;
import com.github.clagomess.tomato.ui.main.collection.node.RequestTreeNode;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class CollectionTreeExpansionListener implements TreeExpansionListener {
    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        CollectionTreeNode node = (CollectionTreeNode) event.getPath().getLastPathComponent();
        if(node.getChildCount() == 0) return;

        DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) node.getChildAt(0);
        if(firstChild instanceof CollectionTreeNode){
            return;
        }

        if(firstChild instanceof RequestTreeNode){
            return;
        }

        SwingUtilities.invokeLater(node::loadChildren);
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
        CollectionTreeNode node = (CollectionTreeNode) event.getPath().getLastPathComponent();
        node.removeAllChildren();
        node.add(new DefaultMutableTreeNode("loading"));
    }
}
