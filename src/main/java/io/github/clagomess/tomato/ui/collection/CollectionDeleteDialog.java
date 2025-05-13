package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.controller.collection.CollectionDeleteDialogController;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;

@RequiredArgsConstructor
public class CollectionDeleteDialog {
    private final Component parent;
    private final CollectionTreeDto collectionTree;
    private final CollectionDeleteDialogController controller;

    private static final String DIALOG_TITLE = "Collection Delete";
    private static final String DIALOG_MESSAGE = "Are you sure you want to delete \"%s\" collection?";

    public CollectionDeleteDialog(
            Component parent,
            CollectionTreeDto collectionTree
    ) {
        this.parent = parent;
        this.collectionTree = collectionTree;
        this.controller = new CollectionDeleteDialogController();
    }

    public void showConfirmDialog(){
        int ret = JOptionPane.showConfirmDialog(
                parent,
                String.format(
                        DIALOG_MESSAGE,
                        collectionTree.getName()
                ),
                DIALOG_TITLE,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if(ret == JOptionPane.OK_OPTION){
            delete();
        }
    }

    private void delete(){
        new WaitExecution(
                parent,
                () -> controller.delete(collectionTree)
        ).execute();
    }
}
