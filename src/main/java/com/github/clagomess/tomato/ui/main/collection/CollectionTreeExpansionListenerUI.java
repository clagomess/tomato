package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.service.CollectionDataService;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Collections;

public class CollectionTreeExpansionListenerUI implements TreeExpansionListener {
    private final DefaultTreeModel treeModel;
    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final CollectionDataService collectionDataService = CollectionDataService.getInstance();

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

            collectionPublisher.getOnSave().addListener(event -> {
                if(!collection.getId().equals(event.getId())){
                    return;
                }

                parent.remove(node); // @TODO: só funciona na primeira interação
                var collectionReplace = collectionDataService.getCollectionTree(
                        collectionTree,
                        collectionTree.getPath()
                ).findFirst().orElseThrow();
                var replaceNode = new DefaultMutableTreeNode(collectionReplace);
                replaceNode.add(new DefaultMutableTreeNode("loading"));
                parent.add(replaceNode);
                treeModel.reload(parent);
            });
        });

        collectionTree.getRequests().forEach(request -> {
            parent.add(new DefaultMutableTreeNode(request));
        });

        requestPublisher.getOnSave().addListener(event -> {
            if(!collectionTree.getId().equals(event.getParent().getId())){
                return;
            }

            Collections.list(parent.children()).stream()
                    .map(item -> (DefaultMutableTreeNode) item)
                    .filter(item -> item.getUserObject() instanceof CollectionTreeDto.Request)
                    .filter(item -> ((CollectionTreeDto.Request) item.getUserObject())
                            .getId().equals(event.getId())
                    )
                    .forEach(parent::remove);

            parent.add(new DefaultMutableTreeNode(event));
            treeModel.reload(parent);
        });
    }
}
