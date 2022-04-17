package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.CollectionDto;
import com.github.clagomess.tomato.dto.EnvironmentDto;
import com.github.clagomess.tomato.ui.MainUi;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CollectionUi extends JPanel {
    //private MainUi parent;
    private List<CollectionDto> collections = new ArrayList<>();
    private List<EnvironmentDto> environments = new ArrayList<>();
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
    private final JComboBox<EnvironmentDto> cbEnvironment = new JComboBox<>();
    private final JTree tree = new JTree(root);

    public CollectionUi() {
        setLayout(new MigLayout("", "[grow, fill]", ""));
        tree.setRootVisible(false);
        tree.setCellRenderer(new CollectionTreeCellRender());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addMouseListener(new CollectionTreeMouseListener(tree, null)); //@TODO: null

        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(getEnv(), "wrap");
        add(scrollPane, "height 100%");
    }

    public void setCollections(List<CollectionDto> collections){
        this.collections = collections;
        this.root.removeAllChildren();

        this.collections.forEach(collection -> {
            DefaultMutableTreeNode collectionNode = new DefaultMutableTreeNode(collection.getName());
            collection.getRequests().forEach(request -> {
                collectionNode.add(new DefaultMutableTreeNode(request));
            });
            this.root.add(collectionNode);
        });

        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.reload();
    }

    public void setEnvironments(List<EnvironmentDto> environments){
        this.environments = environments;
        this.cbEnvironment.removeAllItems();
        this.environments.forEach(cbEnvironment::addItem);
    }

    public JPanel getEnv(){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        cbEnvironment.setForeground(Color.GREEN);
        panel.add(cbEnvironment);

        return panel;
    }
}
