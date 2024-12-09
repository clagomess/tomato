package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.constant.ColorConstant;
import com.github.clagomess.tomato.dto.EnvironmentDto;
import com.github.clagomess.tomato.factory.DialogFactory;
import com.github.clagomess.tomato.service.CollectionDataService;
import com.github.clagomess.tomato.service.WorkspaceDataService;
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
    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("ROOT");
    private final DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    private final JTree tree = new JTree(treeModel);
    private final CollectionTreeExpansionListenerUI collectionTreeExpansionListenerUI = new CollectionTreeExpansionListenerUI(treeModel);

    private final JComboBox<EnvironmentDto> cbEnvironment = new JComboBox<>();
    private final JLabel lblCurrentWorkspace = new JLabel();

    private final WorkspaceDataService workspaceDataService = WorkspaceDataService.getInstance();
    private final CollectionDataService collectionDataService = CollectionDataService.getInstance();

    public CollectionUI() {
        setLayout(new MigLayout("insets 10 10 10 5", "[grow, fill]", ""));
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new CollectionTreeCellRenderUI());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addMouseListener(new CollectionTreeMouseListenerUI(tree));
        tree.addTreeExpansionListener(collectionTreeExpansionListenerUI);

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
        SwingUtilities.invokeLater(this::loadCurrentWorkspace);
//        UIPublisherUtil.getInstance().getSwitchWorkspaceFIList().add((this::setData));
        /* @TODO: check
        UIPublisherUtil.getInstance().getSaveRequestFIList().add(dto -> {
            setCollections(DataService.getInstance().getCurrentWorkspace().getCollections());
        });

         */
    }

    private void loadCurrentWorkspace(){
        try {
            var rootCollection = collectionDataService.getWorkspaceCollectionTree();
            lblCurrentWorkspace.setText(rootCollection.getName());
            rootNode.setUserObject(rootCollection);
            collectionTreeExpansionListenerUI.createLeaf(rootNode, rootCollection);

            this.treeModel.reload();

//        setEnvironments(currentWorkspace.getEnvironments()); @TODO: check
        }catch (Exception e){
            DialogFactory.createDialogException(null, e);
        }
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
