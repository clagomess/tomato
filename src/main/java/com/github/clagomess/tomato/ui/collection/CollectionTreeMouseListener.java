package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.Main;
import com.github.clagomess.tomato.dto.RequestDto;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CollectionTreeMouseListener extends MouseAdapter {
    private final JTree tree;

    public CollectionTreeMouseListener(JTree tree) {
        this.tree = tree;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if (selRow != -1 && e.getClickCount() == 2 && selPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();

            if(selectedNode.getUserObject() instanceof RequestDto){
                RequestDto dto = (RequestDto) selectedNode.getUserObject();
                Main.mainUi.getRequestUi().addNewTab(dto);
            }
        }
    }
}
