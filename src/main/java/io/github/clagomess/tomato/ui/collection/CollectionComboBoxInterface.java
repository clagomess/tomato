package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;

public interface CollectionComboBoxInterface {
    void addItem(CollectionTreeDto item);
    void setSelectedItem(Object item);
}
