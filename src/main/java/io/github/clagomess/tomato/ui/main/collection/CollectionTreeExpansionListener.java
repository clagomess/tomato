package io.github.clagomess.tomato.ui.main.collection;

import io.github.clagomess.tomato.ui.main.collection.node.CollectionTreeNode;
import io.github.clagomess.tomato.ui.main.collection.node.LoadingTreeNode;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;

public class CollectionTreeExpansionListener implements TreeExpansionListener {
    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        CollectionTreeNode node = (CollectionTreeNode) event.getPath().getLastPathComponent();
        if(node.getChildCount() == 0) return;

        if(!(node.getChildAt(0) instanceof LoadingTreeNode)) return;

        node.loadChildren();
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
        CollectionTreeNode node = (CollectionTreeNode) event.getPath().getLastPathComponent();
        node.removeAllChildren();
        node.removeExpandedCollectionsIds(node.getCollection().getId());
        node.add(new LoadingTreeNode());
    }
}
