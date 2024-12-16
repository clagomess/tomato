package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.collection.CollectionImportUI;
import com.github.clagomess.tomato.ui.collection.CollectionNewUI;
import com.github.clagomess.tomato.ui.collection.CollectionRenameUI;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.*;
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
                selectedNode.getUserObject() instanceof RequestHeadDto dto) {
            requestPublisher.getOnLoad().publish(dto);
            return;
        }

        if(e.getButton() == 3 &&
                selectedNode.getUserObject() instanceof RequestHeadDto dto){
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
            addActionListener(ae -> new CollectionNewUI(e.getComponent(), null));
        }});
        popup.show(e.getComponent(), e.getX(), e.getY());
    }

    private void showRequestPopUpMenu(
            MouseEvent e,
            RequestHeadDto dto
    ){
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem("Open"){{
            addActionListener(e -> requestPublisher.getOnLoad().publish(dto));
        }});
        popup.add(new JMenuItem("Open Detached", new BxLinkExternalIcon())); //@TODO: implement
        popup.addSeparator();
        popup.add(new JMenuItem("Move", new BxSortAlt2Icon())); //@TODO: implement
        popup.add(new JMenuItem("Rename"){{
            addActionListener(e -> new RequestRenameUI(tree, dto));
        }});
        popup.addSeparator();
        popup.add(new JMenuItem("Delete", new BxTrashIcon())); //@TODO: implement
        popup.show(e.getComponent(), e.getX(), e.getY());
    }

    private void showCollectionPopUpMenu(
            MouseEvent e,
            CollectionTreeDto dto
    ){
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem("Move", new BxSortAlt2Icon())); //@TODO: implement
        popup.add(new JMenuItem("Rename"){{
            addActionListener(e -> new CollectionRenameUI(tree, dto));
        }});
        popup.addSeparator();
        popup.add(new JMenuItem("New Collection"){{
            addActionListener(ae -> new CollectionNewUI(e.getComponent(), dto));
        }});
        popup.add(new JMenuItem("Import", new BxImportIcon()){{
            addActionListener(ae -> new CollectionImportUI(e.getComponent(), dto));
        }});
        popup.add(new JMenuItem("Export", new BxExportIcon())); //@TODO: implement
        popup.addSeparator();
        popup.add(new JMenuItem("Delete", new BxTrashIcon())); //@TODO: implement
        popup.show(e.getComponent(), e.getX(), e.getY());
    }
}
