package io.github.clagomess.tomato.controller.collection;

import io.github.clagomess.tomato.dto.CollectionPropertiesDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.CollectionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CollectionPropertiesFrameController {
    private final CollectionRepository collectionRepository;

    public CollectionPropertiesFrameController() {
        this.collectionRepository = new CollectionRepository();
    }

    public CollectionPropertiesDto properties(CollectionTreeDto collectionTree) {
        return collectionRepository.properties(collectionTree);
    }
}
