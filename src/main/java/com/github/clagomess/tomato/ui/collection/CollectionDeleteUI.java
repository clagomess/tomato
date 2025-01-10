package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.repository.Repository;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;

@RequiredArgsConstructor
public class CollectionDeleteUI {
    private final Component parent;
    private final CollectionTreeDto collectionTree;

    private final Repository repository = new Repository();
    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();

    public void showConfirmDialog(){
        int ret = JOptionPane.showConfirmDialog(
                parent,
                String.format(
                        "Are you sure you want to delete \"%s\"?",
                        collectionTree.getName()
                ),
                "Collection Delete",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if(ret == JOptionPane.OK_OPTION){
            delete();
        }
    }

    private void delete(){
        new WaitExecution(parent, () -> {
            // @TODO: change to own *repository and apply cache evict
            repository.delete(collectionTree.getPath());

            // update source collection
            collectionPublisher.getOnSave().publish(
                    new CollectionPublisher.ParentCollectionId(
                            collectionTree.getParent().getId()
                    ),
                    collectionTree
            );
        }).execute();
    }
}
