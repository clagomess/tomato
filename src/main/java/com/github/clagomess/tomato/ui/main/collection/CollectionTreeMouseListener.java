package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.collection.CollectionNewUI;
import com.github.clagomess.tomato.ui.collection.CollectionRenameUI;
import com.github.clagomess.tomato.ui.request.RequestRenameUI;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@RequiredArgsConstructor
public class CollectionTreeMouseListener extends MouseAdapter {
    private final JTree tree;
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    @Override
    public void mouseReleased(MouseEvent e) {
        int selRow = tree.getRowForLocation(e.getX(), e.getY());

        if(e.getButton() == 3 && selRow == -1) {
            showDefaultPopupMenu(e);
            return;
        }

        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if(selRow == -1 || selPath == null) return;

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();

        if (e.getButton() == 1 && e.getClickCount() == 2 &&
                selectedNode.getUserObject() instanceof CollectionTreeDto.Request dto) {
            requestPublisher.getOnLoad().publish(dto);
            return;
        }

        if(e.getButton() == 3 &&
                selectedNode.getUserObject() instanceof CollectionTreeDto.Request dto){
            showRequestPopUpMenu(e, dto);
            return;
        }

        if(e.getButton() == 3 &&
                selectedNode.getUserObject() instanceof CollectionTreeDto dto){
            showCollectionPopUpMenu(e, dto);
            return;
        }
    }

    private void showDefaultPopupMenu(MouseEvent e) {
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem("New Request"){{
            addActionListener(e -> requestPublisher.getOnOpenNew().publish(true));
        }});
        popup.add(new JMenuItem("New Collection"){{
            addActionListener(e -> new CollectionNewUI());
        }});
        popup.show(e.getComponent(), e.getX(), e.getY());
    }

    private void showRequestPopUpMenu(
            MouseEvent e,
            CollectionTreeDto.Request dto
    ){
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem("Open"){{
            addActionListener(e -> requestPublisher.getOnLoad().publish(dto));
        }});
        popup.add(new JMenuItem("Open Detached")); //@TODO: implement
        popup.addSeparator();
        popup.add(new JMenuItem("Move")); //@TODO: implement
        popup.add(new JMenuItem("Rename"){{
            addActionListener(e -> new RequestRenameUI(tree, dto));
        }});
        popup.addSeparator();
        popup.add(new JMenuItem("Delete")); //@TODO: implement
        popup.show(e.getComponent(), e.getX(), e.getY());
    }

    private void showCollectionPopUpMenu(
            MouseEvent e,
            CollectionTreeDto dto
    ){
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem("Move")); //@TODO: implement
        popup.add(new JMenuItem("Rename"){{
            addActionListener(e -> new CollectionRenameUI(tree, dto));
        }});
        popup.addSeparator();
        popup.add(new JMenuItem("New Collection"){{
            addActionListener(e -> new CollectionNewUI());
        }});
        popup.add(new JMenuItem("Import")); //@TODO: implement
        popup.add(new JMenuItem("Export")); //@TODO: implement
        popup.addSeparator();
        popup.add(new JMenuItem("Delete")); //@TODO: implement
        popup.show(e.getComponent(), e.getX(), e.getY());
    }
}
