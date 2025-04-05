package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.TreeRepository;
import io.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import io.github.clagomess.tomato.ui.component.ExceptionDialog;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;

import static javax.swing.SwingUtilities.invokeLater;

public class CollectionComboBox extends JComboBox<CollectionTreeDto> {
    private final TreeRepository treeRepository;

    @TestOnly
    protected CollectionComboBox(TreeRepository treeRepository) {
        this.treeRepository = treeRepository;
    }

    public CollectionComboBox(@Nullable CollectionTreeDto selectedCollectionTree) {
        treeRepository = new TreeRepository();
        setRenderer(new DtoListCellRenderer<>(CollectionTreeDto::getFlattenedParentString));
        invokeLater(() -> addItens(selectedCollectionTree));
    }

    protected void addItens(CollectionTreeDto selectedCollectionTree) {
        try {
            treeRepository.getWorkspaceCollectionTree()
                    .flattened()
                    .forEachOrdered(item -> {
                        addItem(item);

                        if(selectedCollectionTree != null &&
                                item.getId().equals(selectedCollectionTree.getId())){
                            setSelectedItem(item);
                        }
                    });
        } catch (Throwable e){
            new ExceptionDialog(this, e);
        }
    }

    @Override
    public CollectionTreeDto getSelectedItem() {
        return (CollectionTreeDto) super.getSelectedItem();
    }
}
