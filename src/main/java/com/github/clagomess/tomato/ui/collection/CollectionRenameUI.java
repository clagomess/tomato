package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.service.CollectionDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.NameUI;

import java.awt.*;

public class CollectionRenameUI extends NameUI {
    private final CollectionDataService collectionDataService = new CollectionDataService();
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
        btnSave.setEnabled(false);

        try {
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
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        } finally {
            btnSave.setEnabled(true);
        }
    }
}
