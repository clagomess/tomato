package io.github.clagomess.tomato.ui.main.collection.node;

import io.github.clagomess.tomato.dto.data.TomatoID;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.publisher.CollectionPublisher;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.key.ParentCollectionKey;
import io.github.clagomess.tomato.ui.component.ExceptionDialog;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;
import static javax.swing.SwingUtilities.invokeLater;

@Setter
@Getter
public class CollectionTreeNode extends DefaultMutableTreeNode {
    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final List<UUID> listenerUuid = new ArrayList<>(0);

    private final JTree tree;
    private final DefaultTreeModel treeModel;
    private final CollectionTreeDto collection;
    private final List<TomatoID> expandedCollectionsIds;

    public CollectionTreeNode(
            JTree tree,
            DefaultTreeModel treeModel,
            CollectionTreeDto collection,
            List<TomatoID> expandedCollectionsIds
    ) {
        super(collection, true);
        add(new LoadingTreeNode());

        this.tree = tree;
        this.treeModel = treeModel;
        this.collection = collection;
        this.expandedCollectionsIds = expandedCollectionsIds;

        addOnChangeListener();
    }

    @Override
    public void setParent(MutableTreeNode newParent) {
        super.setParent(newParent);

        if(newParent == null) {
            removeAllChildren();

            listenerUuid.forEach(uuid -> {
                collectionPublisher.getOnChange().removeListener(uuid);
                requestPublisher.getOnParentCollectionChange().removeListener(uuid);
            });
        }
    }

    @Override
    public void removeAllChildren() {
        super.removeAllChildren();

        listenerUuid.forEach(uuid -> {
            requestPublisher.getOnParentCollectionChange().removeListener(uuid);
        });
    }

    public void removeExpandedCollectionsIds(TomatoID collectionId) {
        expandedCollectionsIds.remove(collectionId);
    }

    public CompletableFuture<Void> loadChildren() {
        var fCollectionList = CompletableFuture.supplyAsync(() -> collection.getChildren().toList());
        var fRequestList = CompletableFuture.supplyAsync(() -> collection.getRequests().toList());

        return CompletableFuture.allOf(fCollectionList, fRequestList)
                .thenAcceptAsync(result -> this.addChildren(
                        fCollectionList.join(),
                        fRequestList.join()
                ), SwingUtilities::invokeLater)
                .exceptionallyAsync(e -> {
                    new ExceptionDialog(null, e);
                    this.removeAllChildren();
                    treeModel.reload(this);
                    return null;
                }, SwingUtilities::invokeLater);
    }

    private void addChildren(
            List<CollectionTreeDto> collectionList,
            List<RequestHeadDto> requestList
    ) {
        this.removeAllChildren();

        List<TreePath> nodePaths = new ArrayList<>(expandedCollectionsIds.size());
        for(var collection : collectionList){
            var node = new CollectionTreeNode(tree, treeModel, collection, expandedCollectionsIds);
            this.add(node);

            if(expandedCollectionsIds.contains(collection.getId())) {
                nodePaths.add(new TreePath(node.getPath()));
            }
        }

        for(var request : requestList){
            this.add(new RequestTreeNode(treeModel, this, request));
        }

        treeModel.reload(this);

        for(var path : nodePaths){
            tree.expandPath(path);
        }

        addRequestOnParentCollectionChangeListener();
    }

    private void addOnChangeListener(){
        var key = new ParentCollectionKey(collection.getId());
        var uuid = collectionPublisher.getOnChange()
                .addListener(key, event -> invokeLater(this::loadChildren));
        listenerUuid.add(uuid);
    }

    private void addRequestOnParentCollectionChangeListener(){
        var key = new ParentCollectionKey(collection.getId());

        var uuid = requestPublisher.getOnParentCollectionChange()
                .addListener(key, event -> {
                    if(event.getType() != INSERTED) return;

                    invokeLater(() -> {
                        this.add(new RequestTreeNode(treeModel, this, event.getEvent()));
                        treeModel.reload(this);
                    });
                });

        listenerUuid.add(uuid);
    }
}
