package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.controller.collection.CollectionComboBoxController;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import io.github.clagomess.tomato.ui.component.ExceptionDialog;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;

import static javax.swing.SwingUtilities.invokeLater;

public class CollectionComboBox
        extends JComboBox<CollectionTreeDto>
        implements CollectionComboBoxInterface {

    public CollectionComboBox(
            @Nullable CollectionTreeDto selectedCollectionTree
    ) {
        setRenderer(new DtoListCellRenderer<>(CollectionTreeDto::getFlattenedParentString));

        invokeLater(() -> {
            try {
                new CollectionComboBoxController(this)
                        .loadItems(selectedCollectionTree);
            } catch (IOException e) {
                new ExceptionDialog(this, e);
            }
        });
    }

    @Override
    public CollectionTreeDto getSelectedItem() {
        return (CollectionTreeDto) super.getSelectedItem();
    }
}
