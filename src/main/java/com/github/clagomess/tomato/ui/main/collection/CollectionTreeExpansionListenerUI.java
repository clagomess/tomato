package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.dto.CollectionTreeDto;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class CollectionTreeExpansionListenerUI implements TreeExpansionListener {
    private final DefaultTreeModel treeModel;

    public CollectionTreeExpansionListenerUI(DefaultTreeModel treeModel) {
        this.treeModel = treeModel;
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

        DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) node.getChildAt(0);
        if(firstChild.getUserObject() instanceof CollectionTreeDto){
            return;
        }

        if(firstChild.getUserObject() instanceof CollectionTreeDto.Request){
            return;
        }

        node.removeAllChildren();
        createLeaf(node, (CollectionTreeDto) node.getUserObject());
        treeModel.reload(node);
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
    }

    public void createLeaf(
            DefaultMutableTreeNode parent,
            CollectionTreeDto collectionTree
    ){
        collectionTree.getChildren().forEach(collection -> {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(collection);
            node.add(new DefaultMutableTreeNode("loading"));
            parent.add(node);
        });

        collectionTree.getRequests().forEach(request -> {
            parent.add(new DefaultMutableTreeNode(request));
        });
    }
}
