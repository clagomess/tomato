package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class CollectionMoveUI extends JFrame {
    private final JButton btnMove = new JButton("Move");
    private final JLabel lblCollectionName = new JLabel();
    private final CollectionComboBox cbCollectionDestination;
    private final CollectionTreeDto collectionTree;

    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();
    private final CollectionRepository collectionRepository = new CollectionRepository();

    public CollectionMoveUI(
            Component parent,
            CollectionTreeDto collectionTree
    ){
        setTitle("Move Collection");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        this.collectionTree = collectionTree;

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
        btnMove.addActionListener(l -> btnMoveAction());
    }

    private void btnMoveAction(){
        new WaitExecution(this, btnMove, () -> {
            CollectionTreeDto destination = cbCollectionDestination.getSelectedItem();
            if(destination == null) throw new Exception("Destination not selected");

            collectionRepository.move(collectionTree, destination);

            // update source collection
            collectionPublisher.getOnSave().publish(
                    new CollectionPublisher.ParentCollectionId(
                            collectionTree.getParent().getId()
                    ),
                    collectionTree
            );

            // update dest collection
            CollectionTreeDto movedCollection = collectionRepository.getCollectionRootTree(
                    destination,
                    collectionTree.getId()
            );

            collectionPublisher.getOnSave().publish(
                    new CollectionPublisher.ParentCollectionId(
                            movedCollection.getParent().getId()
                    ),
                    movedCollection
            );

            setVisible(false);
            dispose();
        }).execute();
    }
}
