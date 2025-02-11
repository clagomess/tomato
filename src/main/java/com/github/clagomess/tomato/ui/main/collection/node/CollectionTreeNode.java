package com.github.clagomess.tomato.ui.main.collection.node;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import lombok.Getter;
import lombok.Setter;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class CollectionTreeNode extends DefaultMutableTreeNode {
    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final List<UUID> listenerUuid = new ArrayList<>(0);

    private final DefaultTreeModel treeModel;
    private final CollectionTreeDto tree;

    public CollectionTreeNode(
            DefaultTreeModel treeModel,
            CollectionTreeDto tree
    ) {
        super(tree, true);
        add(new DefaultMutableTreeNode("loading"));

        this.treeModel = treeModel;
        this.tree = tree;

        addOnSaveListener();
    }

    @Override
    public void setParent(MutableTreeNode newParent) {
        super.setParent(newParent);

        if(newParent == null) {
            removeAllChildren();

            listenerUuid.forEach(uuid -> {
                collectionPublisher.getOnChange().removeListener(uuid);
                requestPublisher.getOnInsert().removeListener(uuid);
            });
        }
    }

    @Override
    public void removeAllChildren() {
        super.removeAllChildren();

        listenerUuid.forEach(uuid -> {
            requestPublisher.getOnInsert().removeListener(uuid);
        });
    }

    public void loadChildren() {
        this.removeAllChildren();

        tree.getChildren().forEach(collection -> {
            this.add(new CollectionTreeNode(treeModel, collection));
        });

        tree.getRequests().forEach(request -> {
            this.add(new RequestTreeNode(treeModel, this, request));
        });

        addOnResquestInsertListener();

        treeModel.reload(this);
    }

    private void addOnSaveListener(){
        var key = new CollectionPublisher.ParentCollectionId(tree.getId());
        var uuid = collectionPublisher.getOnChange().addListener(key, event -> loadChildren());
        listenerUuid.add(uuid);
    }

    private void addOnResquestInsertListener(){
        var key = new RequestPublisher.ParentCollectionId(
                tree.getId()
        );

        var uuid = requestPublisher.getOnInsert().addListener(key, event -> {
            this.add(new RequestTreeNode(treeModel, this, event));
            treeModel.reload(this);
        });

        listenerUuid.add(uuid);
    }
}
