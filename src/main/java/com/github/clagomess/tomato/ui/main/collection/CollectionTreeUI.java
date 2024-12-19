package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.service.CollectionDataService;
import com.github.clagomess.tomato.service.WorkspaceDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxHomeIcon;
import com.github.clagomess.tomato.ui.environment.EnvironmentComboBox;
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
    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("ROOT");
    private final DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    private final JTree tree = new JTree(treeModel);
    private final CollectionTreeExpansionListener collectionTreeExpansionListener = new CollectionTreeExpansionListener(treeModel);

    private final EnvironmentComboBox cbEnvironment = new EnvironmentComboBox();
    private final JLabel lblCurrentWorkspace = new JLabel(new BxHomeIcon());

    private final WorkspaceDataService workspaceDataService = new WorkspaceDataService();
    private final CollectionDataService collectionDataService = new CollectionDataService();
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();

    public CollectionTreeUI() {
        setLayout(new MigLayout(
                "insets 10 10 10 5",
                "[grow, fill]"
        ));
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new CollectionTreeCellRender());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addMouseListener(new CollectionTreeMouseListener(tree));
        tree.addTreeExpansionListener(collectionTreeExpansionListener);

        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(lblCurrentWorkspace, "width 100:200:100% - 14px, wrap");
        add(cbEnvironment, "width 100:200:100% - 14px, wrap");
        add(scrollPane, "height 100%");

        // data
        SwingUtilities.invokeLater(this::loadCurrentWorkspace);
        workspacePublisher.getOnSwitch().addListener(event -> {
            rootNode.removeAllChildren();
            loadCurrentWorkspace();
        });
    }

    private void loadCurrentWorkspace(){
        try {
            var rootCollection = collectionDataService.getWorkspaceCollectionTree();
            lblCurrentWorkspace.setText(rootCollection.getName());
            rootNode.setUserObject(rootCollection);
            collectionTreeExpansionListener.createLeaf(rootNode, rootCollection);

            this.treeModel.reload();
        }catch (Exception e){
            DialogFactory.createDialogException(null, e);
        }
    }
}
