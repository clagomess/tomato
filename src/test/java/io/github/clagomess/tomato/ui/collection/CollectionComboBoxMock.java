package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class CollectionComboBoxMock implements CollectionComboBoxInterface {
    public List<CollectionTreeDto> items = new LinkedList<>();

    @Override
    public void addItem(CollectionTreeDto item) {
        items.add(item);
    }

    @Override
    public void setSelectedItem(Object item) {
        log.debug("Selected item: {}", item);
    }
}
