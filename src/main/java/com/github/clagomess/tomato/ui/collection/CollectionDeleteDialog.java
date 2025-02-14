package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.ParentCollectionKey;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;

@RequiredArgsConstructor
public class CollectionDeleteDialog {
    private final Component parent;
    private final CollectionTreeDto collectionTree;

    private final CollectionRepository collectionRepository = new CollectionRepository();
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
            collectionRepository.delete(collectionTree);

            // update source collection
            collectionPublisher.getOnChange().publish(
                    new ParentCollectionKey(
                            collectionTree.getParent().getId()
                    ),
                    new PublisherEvent<>(DELETED, collectionTree.getId())
            );
        }).execute();
    }
}
