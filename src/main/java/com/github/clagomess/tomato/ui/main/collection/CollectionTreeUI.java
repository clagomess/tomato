package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.io.repository.CollectionRepository;
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

import static javax.swing.SwingUtilities.invokeLater;

@Getter
@Setter
public class CollectionTreeUI extends JPanel {
    private DefaultTreeModel treeModel;
    private JTree tree;
    private final JLabel lblCurrentWorkspace = new JLabel(new BxHomeIcon());

    public CollectionTreeUI() {
        setLayout(new MigLayout(
                "insets 5",
                "[grow, fill]"
        ));

        add(lblCurrentWorkspace, "width 100:200:100% - 14px");

        invokeLater(() -> {
            add(new EnvironmentComboBox(), "cell 0 1, width 100:200:100% - 14px");
            revalidate();
            repaint();
        });

        invokeLater(() -> {
            add(createCollectionTree(), "cell 0 2, height 100%");
            revalidate();
            repaint();
        });

        WorkspacePublisher.getInstance()
                .getOnSwitch()
                .addListener(event -> loadCurrentWorkspace());
    }

    private JScrollPane createCollectionTree() {
        treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("ROOT"));
        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new CollectionTreeCellRender());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addMouseListener(new CollectionTreeMouseListener(tree));
        tree.addTreeExpansionListener(new CollectionTreeExpansionListener());

        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        invokeLater(this::loadCurrentWorkspace);

        return scrollPane;
    }

    private void loadCurrentWorkspace(){
        try {
            var rootCollection = new CollectionRepository().getWorkspaceCollectionTree();

            invokeLater(() -> lblCurrentWorkspace.setText(rootCollection.getName()));

            invokeLater(() -> {
                if(treeModel.getRoot() instanceof CollectionTreeNode rootNode){
                    rootNode.setParent(null);
                }

                var rootNode = new CollectionTreeNode(treeModel, rootCollection);
                treeModel.setRoot(rootNode);
                rootNode.loadChildren();
            });
        }catch (Exception e){
            new ExceptionDialog(null, e);
        }
    }
}
