package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.constant.ColorConstant;
import com.github.clagomess.tomato.dto.CollectionDto;
import com.github.clagomess.tomato.dto.EnvironmentDto;
import com.github.clagomess.tomato.dto.WorkspaceDto;
import com.github.clagomess.tomato.service.DataService;
import com.github.clagomess.tomato.util.UIPublisherUtil;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.List;

@Getter
@Setter
public class CollectionUI extends JPanel {
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
    private final JComboBox<EnvironmentDto> cbEnvironment = new JComboBox<>();
    private final JTree tree = new JTree(root);
    private final JLabel lblCurrentWorkspace = new JLabel();

    public CollectionUI() {
        setLayout(new MigLayout("insets 10 10 10 5", "[grow, fill]", ""));
        tree.setRootVisible(false);
        tree.setCellRenderer(new CollectionTreeCellRenderUI());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addMouseListener(new CollectionTreeMouseListenerUI(tree));

        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel pWorkspace = new JPanel();
        pWorkspace.setLayout(new BorderLayout());
        pWorkspace.add(lblCurrentWorkspace);
        pWorkspace.setBackground(ColorConstant.GRAY);

        add(pWorkspace, "wrap");
        add(getEnv(), "wrap");
        add(scrollPane, "height 100%");

        // data
        setData(DataService.getInstance().getCurrentWorkspace());
        UIPublisherUtil.getInstance().getSwitchWorkspaceFIList().add((this::setData));
    }

    private void setData(WorkspaceDto currentWorkspace){
        lblCurrentWorkspace.setText(currentWorkspace.getName());
        setCollections(currentWorkspace.getCollections());
        setEnvironments(currentWorkspace.getEnvironments());
    }

    private void setCollections(List<CollectionDto> collections){
        this.root.removeAllChildren();

        collections.forEach(collection -> {
            DefaultMutableTreeNode collectionNode = new DefaultMutableTreeNode(collection.getName());
            collection.getRequests().forEach(request -> {
                collectionNode.add(new DefaultMutableTreeNode(request));
            });
            this.root.add(collectionNode);
        });

        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.reload();
    }

    private void setEnvironments(List<EnvironmentDto> environments){
        this.cbEnvironment.removeAllItems();
        environments.forEach(cbEnvironment::addItem);
    }

    public JPanel getEnv(){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        cbEnvironment.setForeground(Color.GREEN);
        panel.add(cbEnvironment);

        return panel;
    }
}
