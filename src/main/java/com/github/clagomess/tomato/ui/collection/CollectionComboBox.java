package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.service.CollectionDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.DtoListCellRenderer;

import javax.swing.*;

public class CollectionComboBox extends JComboBox<CollectionTreeDto> {
    private final CollectionDataService collectionDataService = new CollectionDataService();

    public CollectionComboBox(CollectionTreeDto selectedCollectionTree) {
        setRenderer(new DtoListCellRenderer<>(CollectionTreeDto::getFlattenedParentString));
        SwingUtilities.invokeLater(() -> addItens(selectedCollectionTree));
    }

    private void addItens(CollectionTreeDto selectedCollectionTree) {
        try {
            collectionDataService.getWorkspaceCollectionTree()
                    .flattened()
                    .forEach(this::addItem);

            if(selectedCollectionTree != null){
                setSelectedItem(selectedCollectionTree);
            }
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        }
    }

    @Override
    public CollectionTreeDto getSelectedItem() {
        return (CollectionTreeDto) super.getSelectedItem();
    }
}
