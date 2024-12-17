package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.service.CollectionDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;

import javax.swing.*;
import java.awt.*;

public class CollectionComboBox extends JComboBox<CollectionTreeDto> {
    private final CollectionDataService collectionDataService = new CollectionDataService();

    public CollectionComboBox(CollectionTreeDto selectedCollectionTree) {
        setRenderer(new HierarchyRenderer());
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

    protected static class HierarchyRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus
            );

            if(value != null) {
                label.setText(((CollectionTreeDto) value).getFlattenedParentString());
            }

            return label;
        }
    }
}
