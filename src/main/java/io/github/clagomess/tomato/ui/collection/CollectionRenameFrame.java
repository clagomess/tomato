package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.controller.collection.CollectionRenameFrameController;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.ui.component.NameFrame;
import io.github.clagomess.tomato.ui.component.WaitExecution;

import java.awt.*;

public class CollectionRenameFrame extends NameFrame {
    private final CollectionRenameFrameController controller;

    public CollectionRenameFrame(
            Component parent,
            CollectionTreeDto collectionTree
    ) {
        super(parent);
        controller = new CollectionRenameFrameController();

        setTitle("Collection Request");
        txtName.setText(collectionTree.getName());
        btnSave.addActionListener(l -> btnSaveAction(collectionTree));
    }

    private void btnSaveAction(
            CollectionTreeDto collectionTree
    ){
        new WaitExecution(this, btnSave, () -> {
            controller.save(
                    collectionTree,
                    this.txtName.getText()
            );

            setVisible(false);
            dispose();
        }).execute();
    }
}
