package com.github.clagomess.tomato.ui.main.collection;

import com.formdev.flatlaf.icons.FlatFileViewDirectoryIcon;
import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.ui.component.IconFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class CollectionTreeCellRenderUI extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus
    ) {
        Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

        if(userObject instanceof CollectionTreeDto.Request dto){
            return new JLabel(
                    dto.getName(),
                    IconFactory.createHttpMethodIcon(dto.getMethod()),
                    SwingConstants.LEFT

            );
        }

        if(userObject instanceof CollectionTreeDto dto){
            return new JLabel(
                    dto.getName(),
                    new FlatFileViewDirectoryIcon(),
                    SwingConstants.LEFT
            );
        }

        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }
}
