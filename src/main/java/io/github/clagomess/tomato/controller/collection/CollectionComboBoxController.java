package io.github.clagomess.tomato.controller.collection;

import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.TreeRepository;
import io.github.clagomess.tomato.ui.collection.CollectionComboBoxInterface;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class CollectionComboBoxController {
    private final TreeRepository treeRepository;
    private final CollectionComboBoxInterface ui;

    public CollectionComboBoxController(
            CollectionComboBoxInterface collectionComboBoxInterface
    ) {
        this.ui = collectionComboBoxInterface;
        this.treeRepository = new TreeRepository();
    }

    public void loadItems(CollectionTreeDto selected) throws IOException {
        treeRepository.getWorkspaceCollectionTree()
                .flattened()
                .forEachOrdered(item -> {
                    ui.addItem(item);

                    if (selected != null && item.getId().equals(selected.getId())) {
                        ui.setSelectedItem(item);
                    }
                });
    }
}
