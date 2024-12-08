package com.github.clagomess.tomato.ui.component;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.factory.DialogFactory;
import com.github.clagomess.tomato.service.CollectionDataService;

import javax.swing.*;
import java.awt.*;

public class CollectionComboBoxComponent extends JComboBox<CollectionTreeDto> {
    private final CollectionDataService collectionDataService = CollectionDataService.getInstance();

    public CollectionComboBoxComponent() {
        setRenderer(new HierarchyRenderer());
        SwingUtilities.invokeLater(this::addItens);
    }

    private void addItens() {
        try {
            collectionDataService.getWorkspaceCollectionTree()
                    .flattened()
                    .forEach(this::addItem);
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
            return new JLabel(((CollectionTreeDto) value).flattenedParentString());
        }
    }
}
