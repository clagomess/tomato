package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.ui.component.NameUI;
import com.github.clagomess.tomato.ui.component.WaitExecution;

import java.awt.*;

public class CollectionRenameUI extends NameUI {
    private final CollectionRepository collectionDataService = new CollectionRepository();
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
            CollectionDto collectionDto = collectionDataService.load(collectionTree)
                    .orElseThrow();

            collectionDto.setName(this.txtName.getText());

            collectionDataService.save(
                    collectionTree.getParent().getPath(),
                    collectionDto
            );

            var key = new CollectionPublisher.ParentCollectionId(collectionTree.getParent().getId());
            collectionPublisher.getOnSave().publish(
                    key,
                    collectionTree
            );

            setVisible(false);
            dispose();
        }).execute();
    }
}
