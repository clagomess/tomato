package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.controller.collection.CollectionMoveFrameController;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class CollectionMoveFrame extends JFrame {
    private final JButton btnMove = new JButton("Move");
    private final JLabel lblCollectionName = new JLabel();
    private final CollectionComboBox cbCollectionDestination;

    private final CollectionMoveFrameController controller;

    public CollectionMoveFrame(
            Component parent,
            CollectionTreeDto collectionTree
    ){
        this.controller = new CollectionMoveFrameController();

        setTitle("Move Collection");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        lblCollectionName.setText(collectionTree.getName());
        cbCollectionDestination = new CollectionComboBox(collectionTree.getParent());

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("Collection"), "wrap");
        add(lblCollectionName, "width 300!, wrap");
        add(new JLabel("Parent"), "wrap");
        add(cbCollectionDestination, "width 300!, wrap");
        add(btnMove, "align right");

        getRootPane().setDefaultButton(btnMove);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnMove.addActionListener(l -> btnMoveAction(collectionTree));
    }

    private void btnMoveAction(
            CollectionTreeDto source
    ){
        new WaitExecution(this, btnMove, () -> {
            controller.move(
                    source,
                    cbCollectionDestination.getSelectedItem()
            );

            setVisible(false);
            dispose();
        }).execute();
    }
}
