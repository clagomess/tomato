package io.github.clagomess.tomato.ui.main.collection;

import io.github.clagomess.tomato.ui.main.collection.node.CollectionTreeNode;
import io.github.clagomess.tomato.ui.main.collection.node.RequestTreeNode;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Slf4j
public class CollectionKeyboardAction implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        if(!(e.getSource() instanceof JTree tree)) return;

        TreePath selPath = tree.getSelectionPath();
        if(selPath == null) return;

        Object selectedNode = selPath.getLastPathComponent();

        if(selectedNode instanceof RequestTreeNode node){
            copyToClipboard(node.getRequestHead().getName());
        }

        if(selectedNode instanceof CollectionTreeNode node){
            copyToClipboard(node.getCollection().getName());
        }
    }

    private void copyToClipboard(String value){
        log.info("Copied to clipboard: {}", value);

        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(
                        value
                ), null);
    }
}
