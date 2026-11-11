package io.github.clagomess.tomato.ui.main.collection;

import com.formdev.flatlaf.util.SystemInfo;
import io.github.clagomess.tomato.controller.main.collection.CollectionTreeController;
import io.github.clagomess.tomato.dto.data.TomatoID;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.ui.component.ExceptionDialog;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxHomeIcon;
import io.github.clagomess.tomato.ui.environment.EnvironmentSwitcherComboBox;
import io.github.clagomess.tomato.ui.main.collection.node.CollectionTreeNode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;

import static javax.swing.SwingUtilities.invokeLater;

@Slf4j
@Getter
@Setter
public class CollectionTree extends JPanel {
    private static final Icon HOME_ICON = new BxHomeIcon();

    private final DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("ROOT"));
    private final JTree tree = new JTree(treeModel);
    private final JLabel lblCurrentWorkspace = new JLabel(HOME_ICON, SwingConstants.LEFT);

    private final CollectionTreeController controller = new CollectionTreeController();

    public CollectionTree() {
        setLayout(new MigLayout(
                "insets 5",
                "[grow, fill]"
        ));

        add(
                lblCurrentWorkspace,
                "width 100:200:100% - 14px" +
                (SystemInfo.isMacFullWindowContentSupported ? ", gaptop 20px" : "")
        );

        invokeLater(() -> {
            add(new EnvironmentSwitcherComboBox(), "cell 0 1, width 100:200:100% - 14px");
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
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new CollectionTreeCellRender());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addMouseListener(new CollectionTreeMouseListener(tree));
        tree.addTreeExpansionListener(new CollectionTreeExpansionListener());
        tree.registerKeyboardAction(
                new CollectionKeyboardAction(),
                KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                JComponent.WHEN_FOCUSED
        );

        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        ForkJoinPool.commonPool().submit(this::loadCurrentWorkspace);
        controller.addSaveTreeExpandedStateListener(this::getExpandedCollectionsIds);

        return scrollPane;
    }

    private List<TomatoID> getExpandedCollectionsIds() {
        if(!SwingUtilities.isEventDispatchThread()) {
            var result = new AtomicReference<List<TomatoID>>(List.of());

            try {
                SwingUtilities.invokeAndWait(() -> result.set(getExpandedCollectionsIds()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (InvocationTargetException e) {
                log.error(e.getMessage(), e);
            }

            return result.get();
        }

        Set<TomatoID> expandedCollectionsIds = new TreeSet<>();

        Enumeration<TreePath> expanded = tree.getExpandedDescendants(
                new TreePath(tree.getModel().getRoot())
        );

        while (expanded != null && expanded.hasMoreElements()) {
            TreePath path = expanded.nextElement();

            if(path.getLastPathComponent() instanceof CollectionTreeNode treeNode){
                expandedCollectionsIds.add(treeNode.getCollection().getId());
            }
        }

        return expandedCollectionsIds.stream().toList();
    }

    private void loadCurrentWorkspace(){
        try {
            CollectionTreeDto rootCollection = controller.loadCurrentWorkspace(workspace ->
                invokeLater(() -> lblCurrentWorkspace.setText(workspace.getName()))
            );

            List<TomatoID> expandedCollectionsIds = controller.loadWorkspaceSession()
                    .getExpandedCollectionsIds();

            invokeLater(() -> {
                lblCurrentWorkspace.setText(rootCollection.getName());

                if(treeModel.getRoot() instanceof CollectionTreeNode rootNode){
                    rootNode.setParent(null);
                }

                var rootNode = new CollectionTreeNode(
                        tree,
                        treeModel,
                        rootCollection,
                        expandedCollectionsIds
                );
                treeModel.setRoot(rootNode);
                rootNode.loadChildren();
            });
        }catch (Exception e){
            new ExceptionDialog(null, e);
        }
    }
}
