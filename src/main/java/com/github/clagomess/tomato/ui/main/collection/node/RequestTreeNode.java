package com.github.clagomess.tomato.ui.main.collection.node;

import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.Getter;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.util.UUID;

@Getter
public class RequestTreeNode extends DefaultMutableTreeNode {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final UUID listenerUuid;

    private final DefaultTreeModel treeModel;
    private final CollectionTreeNode parentNode;
    private final RequestHeadDto head;
    private final RequestKey requestKey;

    public RequestTreeNode(
            DefaultTreeModel treeModel,
            CollectionTreeNode parentNode,
            RequestHeadDto head
    ) {
        super(head, false);
        this.treeModel = treeModel;
        this.parentNode = parentNode;
        this.head = head;
        this.requestKey = new RequestKey(
                head.getParent().getId(),
                head.getId()
        );

        this.listenerUuid = requestPublisher.getOnChange().addListener(requestKey, event -> {
            switch (event.getType()){
                case UPDATED -> onUpdate(event.getEvent());
                case DELETED -> onDelete();
            }
        });
    }

    @Override
    public void setParent(MutableTreeNode newParent) {
        super.setParent(newParent);
        if(newParent == null) {
            requestPublisher.getOnChange().removeListener(listenerUuid);
        }
    }

    private void onUpdate(RequestHeadDto requestHead) {
        parentNode.insert(
                new RequestTreeNode(treeModel, parentNode, requestHead),
                parentNode.getIndex(this)
        );

        removeFromParent();

        treeModel.reload(parentNode);
    }

    private void onDelete(){
        removeFromParent();
        treeModel.reload(parentNode);
    }
}
