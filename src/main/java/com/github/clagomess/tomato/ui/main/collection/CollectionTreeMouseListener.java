package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.ui.main.collection.popupmenu.CollectionPopUpMenu;
import com.github.clagomess.tomato.ui.main.collection.popupmenu.DefaultPopupMenu;
import com.github.clagomess.tomato.ui.main.collection.popupmenu.RequestPopUpMenu;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.LOAD;
import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON3;

@RequiredArgsConstructor
public class CollectionTreeMouseListener extends MouseAdapter {
    private final JTree tree;
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    @Override
    public void mouseReleased(MouseEvent e) {
        int selRow = tree.getRowForLocation(e.getX(), e.getY());

        if(e.getButton() == BUTTON3 && selRow == -1) {
            new DefaultPopupMenu(e.getComponent())
                    .show(e.getComponent(), e.getX(), e.getY());
            return;
        }

        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if(selRow == -1 || selPath == null) return;

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();

        if (e.getButton() == BUTTON1 && e.getClickCount() == 2 &&
                selectedNode.getUserObject() instanceof RequestHeadDto dto) {
            requestPublisher.getOnLoad().publish(new PublisherEvent<>(LOAD, dto));
            return;
        }

        if(e.getButton() == BUTTON3 &&
                selectedNode.getUserObject() instanceof RequestHeadDto dto){
            new RequestPopUpMenu(tree, dto)
                    .show(e.getComponent(), e.getX(), e.getY());
            return;
        }

        if(e.getButton() == BUTTON3 &&
                selectedNode.getUserObject() instanceof CollectionTreeDto dto){
            new CollectionPopUpMenu(tree, dto)
                    .show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
