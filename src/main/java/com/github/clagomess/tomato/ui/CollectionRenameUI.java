package com.github.clagomess.tomato.ui;

import com.github.clagomess.tomato.dto.CollectionDto;
import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.factory.DialogFactory;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.service.CollectionDataService;
import com.github.clagomess.tomato.ui.component.RenameComponent;

import java.awt.*;

public class CollectionRenameUI extends RenameComponent {
    private final CollectionDataService collectionDataService = CollectionDataService.getInstance();
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
            collectionTree.setName(this.txtName.getText());

            collectionDataService.save(
                    collectionTree.getParent().getPath(),
                    collectionDto
            );

            collectionPublisher.getOnSave().publish(collectionTree);

            setVisible(false);
            dispose();
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        } finally {
            btnSave.setEnabled(true);
        }
    }
}
