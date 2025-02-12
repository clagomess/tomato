package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;

import static javax.swing.SwingUtilities.invokeLater;

public class CollectionComboBox extends JComboBox<CollectionTreeDto> {
    private final CollectionRepository collectionRepository;

    @TestOnly
    protected CollectionComboBox(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public CollectionComboBox(@Nullable CollectionTreeDto selectedCollectionTree) {
        collectionRepository = new CollectionRepository();
        setRenderer(new DtoListCellRenderer<>(CollectionTreeDto::getFlattenedParentString));
        invokeLater(() -> addItens(selectedCollectionTree));
    }

    protected void addItens(CollectionTreeDto selectedCollectionTree) {
        try {
            collectionRepository.getWorkspaceCollectionTree()
                    .flattened()
                    .forEachOrdered(this::addItem);

            if(selectedCollectionTree != null){
                setSelectedItem(selectedCollectionTree);
            }
        } catch (Throwable e){
            new ExceptionDialog(this, e);
        }
    }

    @Override
    public CollectionTreeDto getSelectedItem() {
        return (CollectionTreeDto) super.getSelectedItem();
    }
}
