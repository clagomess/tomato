package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.factory.IconFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class CollectionTreeCellRender extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

        if(userObject instanceof RequestDto){
            RequestDto dto = (RequestDto) userObject;

            JLabel label = new JLabel(dto.getName());
            label.setIcon(IconFactory.createHttpMethodIcon(dto.getMethod()));
            return label;
        }

        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }
}
