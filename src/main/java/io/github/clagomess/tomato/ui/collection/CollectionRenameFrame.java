package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.dto.data.CollectionDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.CollectionRepository;
import io.github.clagomess.tomato.publisher.CollectionPublisher;
import io.github.clagomess.tomato.publisher.base.EventTypeEnum;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.ParentCollectionKey;
import io.github.clagomess.tomato.ui.component.NameFrame;
import io.github.clagomess.tomato.ui.component.WaitExecution;

import java.awt.*;

public class CollectionRenameFrame extends NameFrame {
    private final CollectionRepository collectionRepository = new CollectionRepository();
    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();

    public CollectionRenameFrame(
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

            var key = new ParentCollectionKey(collectionTree.getParent().getId());
            collectionPublisher.getOnChange().publish(
                    key,
                    new PublisherEvent<>(EventTypeEnum.UPDATED, collectionTree.getId())
            );

            setVisible(false);
            dispose();
        }).execute();
    }
}
