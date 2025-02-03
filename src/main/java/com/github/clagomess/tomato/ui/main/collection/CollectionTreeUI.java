package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.io.repository.WorkspaceRepository;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxHomeIcon;
import com.github.clagomess.tomato.ui.environment.EnvironmentComboBox;
import com.github.clagomess.tomato.ui.main.collection.node.CollectionTreeNode;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

@Getter
@Setter
public class CollectionTreeUI extends JPanel {
    private final DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("ROOT"));
    private final JTree tree = new JTree(treeModel);

    private final EnvironmentComboBox cbEnvironment = new EnvironmentComboBox();
    private final JLabel lblCurrentWorkspace = new JLabel(new BxHomeIcon());

    private final WorkspaceRepository workspaceRepository = new WorkspaceRepository();
    private final CollectionRepository collectionRepository = new CollectionRepository();
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();

    public CollectionTreeUI() {
        setLayout(new MigLayout(
                "insets 5",
                "[grow, fill]"
        ));
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new CollectionTreeCellRender());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addMouseListener(new CollectionTreeMouseListener(tree));
        tree.addTreeExpansionListener(new CollectionTreeExpansionListener());

        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(lblCurrentWorkspace, "width 100:200:100% - 14px, wrap");
        add(cbEnvironment, "width 100:200:100% - 14px, wrap");
        add(scrollPane, "height 100%");

        // data
        new Thread(
                this::loadCurrentWorkspace,
                getClass().getSimpleName()
        ).start();

        workspacePublisher.getOnSwitch().addListener(event -> {
            loadCurrentWorkspace();
        });
    }

    private void loadCurrentWorkspace(){
        try {
            var rootCollection = collectionRepository.getWorkspaceCollectionTree();
            lblCurrentWorkspace.setText(rootCollection.getName());

            if(treeModel.getRoot() instanceof CollectionTreeNode rootNode){
                rootNode.setParent(null);
            }

            var rootNode = new CollectionTreeNode(treeModel, rootCollection);
            treeModel.setRoot(rootNode);
            rootNode.loadChildren();
        }catch (Exception e){
            new ExceptionDialog(null, e);
        }
    }
}
