package io.github.clagomess.tomato.ui.main.collection;

import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxFolderIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxFolderOpenIcon;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class CollectionTreeCellRender extends DefaultTreeCellRenderer {
    private static final Icon FOLDER_ICON = new BxFolderIcon();
    private static final Icon FOLDER_OPEN_ICON = new BxFolderOpenIcon();

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

        if(userObject instanceof RequestHeadDto dto){
            return new JLabel(
                    dto.getName(),
                    dto.getMethod().getIcon(),
                    SwingConstants.LEFT

            );
        }

        if(userObject instanceof CollectionTreeDto dto){
            return new JLabel(
                    dto.getName(),
                    expanded ? FOLDER_OPEN_ICON : FOLDER_ICON,
                    SwingConstants.LEFT
            );
        }

        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }
}
