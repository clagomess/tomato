package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.publisher.base.EventTypeEnum;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.ui.component.NameUI;
import com.github.clagomess.tomato.ui.component.WaitExecution;

import java.awt.*;

public class CollectionRenameUI extends NameUI {
    private final CollectionRepository collectionRepository = new CollectionRepository();
    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();

    public CollectionRenameUI(
            Component parent,
            CollectionTreeDto collectionTree
    ) {
        super(parent);
        setTitle("Collection Request");
        txtName.setText(collectionTree.getName());
        btnSave.addActionListener(l -> btnSaveAction(collectionTree));
    }

    private void btnSaveAction(CollectionTreeDto collectionTree){
        new WaitExecution(this, btnSave, () -> {
            CollectionDto collectionDto = collectionRepository.load(collectionTree)
                    .orElseThrow();

            collectionDto.setName(this.txtName.getText());

            collectionRepository.save(
                    collectionTree.getParent().getPath(),
                    collectionDto
            );

            var key = new CollectionPublisher.ParentCollectionId(collectionTree.getParent().getId());
            collectionPublisher.getOnChange().publish(
                    key,
                    new PublisherEvent<>(EventTypeEnum.UPDATED, collectionTree.getId())
            );

            setVisible(false);
            dispose();
        }).execute();
    }
}
