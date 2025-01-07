package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.publisher.RequestPublisher;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Collections;

public class CollectionTreeExpansionListener implements TreeExpansionListener {
    private final DefaultTreeModel treeModel;
    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public CollectionTreeExpansionListener(DefaultTreeModel treeModel) {
        this.treeModel = treeModel;
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

        DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) node.getChildAt(0);
        if(firstChild.getUserObject() instanceof CollectionTreeDto){
            return;
        }

        if(firstChild.getUserObject() instanceof RequestHeadDto){
            return;
        }

        SwingUtilities.invokeLater(() -> {
            node.removeAllChildren();
            createLeaf(node, (CollectionTreeDto) node.getUserObject());
            treeModel.reload(node);
        });
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
    }

    public void createLeaf(
            DefaultMutableTreeNode parentNode,
            CollectionTreeDto collectionTree
    ){
        collectionTree.getChildren().forEach(collection -> {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(collection);
            node.add(new DefaultMutableTreeNode("loading"));
            parentNode.add(node);

            collectionAddOnSaveListener(node, new CollectionPublisher.ParentCollectionId(
                    collection.getId()
            ));
        });

        collectionTree.getRequests().forEach(request -> {
            parentNode.add(new DefaultMutableTreeNode(request));

            requestAddOnUpdateListener(parentNode, new RequestPublisher.RequestKey(
                    collectionTree.getId(),
                    request.getId()
            ));
        });

        collectionAddOnSaveListener(parentNode, new CollectionPublisher.ParentCollectionId(
                collectionTree.getId()
        ));
        requestAddOnInsertListener(parentNode, new RequestPublisher.ParentCollectionId(
                collectionTree.getId()
        ));
        requestAddOnMoveListener(parentNode, new RequestPublisher.ParentCollectionId(
                collectionTree.getId()
        ));
    }

    private void collectionAddOnSaveListener(
            DefaultMutableTreeNode parentNode,
            CollectionPublisher.ParentCollectionId parentCollectionId
    ){
        collectionPublisher.getOnSave().removeListener(parentCollectionId);
        collectionPublisher.getOnSave().addListener(parentCollectionId, event -> {
            parentNode.removeAllChildren();
            createLeaf(parentNode, event.getParent());
            treeModel.reload(parentNode);
        });
    }

    private void requestAddOnInsertListener(
            DefaultMutableTreeNode parentNode,
            RequestPublisher.ParentCollectionId parentCollectionId
    ){
        requestPublisher.getOnInsert().removeListener(parentCollectionId);
        requestPublisher.getOnInsert().addListener(parentCollectionId, event -> {
            parentNode.add(new DefaultMutableTreeNode(event));
            treeModel.reload(parentNode);
        });
    }

    private void requestAddOnUpdateListener(
            DefaultMutableTreeNode parentNode,
            RequestPublisher.RequestKey key
    ){
        requestPublisher.getOnUpdate().removeListener(key);
        requestPublisher.getOnUpdate().addListener(key, event -> {
            Collections.list(parentNode.children()).stream()
                    .map(item -> (DefaultMutableTreeNode) item)
                    .filter(item -> {
                        if(item.getUserObject() instanceof RequestHeadDto dto){
                            return dto.getId().equals(event.getId());
                        }

                        return false;
                    })
                    .forEach(parentNode::remove);

            parentNode.add(new DefaultMutableTreeNode(event));
            treeModel.reload(parentNode);
        });
    }

    private void requestAddOnMoveListener(
            DefaultMutableTreeNode parentNode,
            RequestPublisher.ParentCollectionId parentCollectionId
    ){
        requestPublisher.getOnMove().removeListener(parentCollectionId);
        requestPublisher.getOnMove().addListener(parentCollectionId, event -> {
            Collections.list(parentNode.children()).stream()
                    .map(item -> (DefaultMutableTreeNode) item)
                    .filter(item -> {
                        if(item.getUserObject() instanceof RequestHeadDto dto){
                            return dto.getId().equals(event.requestId());
                        }

                        return false;
                    })
                    .forEach(parentNode::remove);

            treeModel.reload(parentNode);
        });
    }
}
