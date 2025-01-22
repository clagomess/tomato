package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;

import javax.swing.*;

import static javax.swing.SwingUtilities.invokeLater;

public class CollectionComboBox extends JComboBox<CollectionTreeDto> {
    private final CollectionRepository collectionRepository = new CollectionRepository();

    public CollectionComboBox(CollectionTreeDto selectedCollectionTree) {
        setRenderer(new DtoListCellRenderer<>(CollectionTreeDto::getFlattenedParentString));
        addItens(selectedCollectionTree);
    }

    private void addItens(CollectionTreeDto selectedCollectionTree) {
        new Thread(() -> {
            try {
                collectionRepository.getWorkspaceCollectionTree()
                        .flattened()
                        .forEach(item -> invokeLater(() -> addItem(item)));

                if(selectedCollectionTree != null){
                    invokeLater(() -> setSelectedItem(selectedCollectionTree));
                }
            } catch (Throwable e){
                invokeLater(() -> new ExceptionDialog(this, e));
            }
        }, "CollectionComboBox").start();
    }

    @Override
    public CollectionTreeDto getSelectedItem() {
        return (CollectionTreeDto) super.getSelectedItem();
    }
}
