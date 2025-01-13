package com.github.clagomess.tomato.ui.main.collection.node;

import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import lombok.Getter;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class RequestTreeNode extends DefaultMutableTreeNode {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final List<UUID> listenerUuid = new ArrayList<>(0);

    private final DefaultTreeModel treeModel;
    private final CollectionTreeNode parentNode;
    private final RequestHeadDto head;

    public RequestTreeNode(
            DefaultTreeModel treeModel,
            CollectionTreeNode parentNode,
            RequestHeadDto head
    ) {
        super(head, false);
        this.treeModel = treeModel;
        this.parentNode = parentNode;
        this.head = head;

        addOnUpdateListener();
        addOnMoveListener();
    }

    @Override
    public void setParent(MutableTreeNode newParent) {
        super.setParent(newParent);
        if(newParent == null) {
            listenerUuid.forEach(uuid -> {
                requestPublisher.getOnUpdate().removeListener(uuid);
                requestPublisher.getOnMove().removeListener(uuid);
            });
        }
    }

    private void addOnUpdateListener(){
        var key = new RequestPublisher.RequestKey(
                head.getParent().getId(),
                head.getId()
        );

        var uuid = requestPublisher.getOnUpdate().addListener(key, event -> {
            parentNode.insert(
                    new RequestTreeNode(treeModel, parentNode, event),
                    parentNode.getIndex(this)
            );

            removeFromParent();

            treeModel.reload(parentNode);
        });

        listenerUuid.add(uuid);
    }

    private void addOnMoveListener(){
        var key = new RequestPublisher.ParentCollectionId(
                parentNode.getTree().getId()
        );

        var uuid = requestPublisher.getOnMove().addListener(key, event -> {
            Collections.list(parentNode.children()).stream()
                    .filter(item -> item instanceof RequestTreeNode)
                    .map(item -> (RequestTreeNode) item)
                    .filter(item -> item.getHead().getId().equals(event.requestId()))
                    .forEach(parentNode::remove);

            treeModel.reload(parentNode);
        });

        listenerUuid.add(uuid);
    }
}
