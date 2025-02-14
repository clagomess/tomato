package com.github.clagomess.tomato.ui.main.collection;

import com.github.clagomess.tomato.controller.main.collection.CollectionTreeController;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
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
import java.util.concurrent.ForkJoinPool;

import static javax.swing.SwingUtilities.invokeLater;

@Getter
@Setter
public class CollectionTree extends JPanel {
    private DefaultTreeModel treeModel;
    private JTree tree;
    private final JLabel lblCurrentWorkspace = new JLabel(new BxHomeIcon());

    private final CollectionTreeController controller = new CollectionTreeController();

    public CollectionTree() {
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

        controller.addOnSwitchListener(this::loadCurrentWorkspace);
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

        ForkJoinPool.commonPool().submit(this::loadCurrentWorkspace);

        return scrollPane;
    }

    private void loadCurrentWorkspace(){
        try {
            CollectionTreeDto rootCollection = controller.loadCurrentWorkspace(workspace ->
                invokeLater(() -> lblCurrentWorkspace.setText(workspace.getName()))
            );

            invokeLater(() -> {
                lblCurrentWorkspace.setText(rootCollection.getName());

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
