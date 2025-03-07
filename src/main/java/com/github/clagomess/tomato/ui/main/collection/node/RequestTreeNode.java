package com.github.clagomess.tomato.ui.main.collection.node;

import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.Getter;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.util.UUID;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isEventDispatchThread;

@Getter
public class RequestTreeNode extends DefaultMutableTreeNode {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final UUID listenerUuid;

    private final DefaultTreeModel treeModel;
    private final RequestKey requestKey;

    public RequestTreeNode(
            DefaultTreeModel treeModel,
            CollectionTreeNode parentNode,
            RequestHeadDto head
    ) {
        super(head, false);
        this.treeModel = treeModel;
        this.requestKey = new RequestKey(
                head.getParent().getId(),
                head.getId()
        );

        this.listenerUuid = requestPublisher.getOnChange().addListener(requestKey, event -> {
            switch (event.getType()){
                case UPDATED -> invokeLater(() -> onUpdate(parentNode, this, event.getEvent()));
                case DELETED -> invokeLater(() -> onDelete(parentNode, this));
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

    private void onUpdate(
            CollectionTreeNode parentNode,
            RequestTreeNode node,
            RequestHeadDto requestHead
    ) {
        if(!isEventDispatchThread()) throw new IllegalThreadStateException();

        int nodeIdx = parentNode.getIndex(node);
        node.removeFromParent();

        parentNode.insert(
                new RequestTreeNode(treeModel, parentNode, requestHead),
                nodeIdx
        );

        treeModel.reload(parentNode);
    }

    private void onDelete(
            CollectionTreeNode parentNode,
            RequestTreeNode node
    ){
        if(!isEventDispatchThread()) throw new IllegalThreadStateException();

        node.removeFromParent();
        treeModel.reload(parentNode);
    }
}
