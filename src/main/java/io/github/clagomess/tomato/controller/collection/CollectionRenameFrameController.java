package io.github.clagomess.tomato.controller.collection;

import io.github.clagomess.tomato.dto.data.CollectionDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.CollectionRepository;
import io.github.clagomess.tomato.publisher.CollectionPublisher;
import io.github.clagomess.tomato.publisher.base.EventTypeEnum;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.ParentCollectionKey;

import java.io.IOException;

public class CollectionRenameFrameController {
    private final CollectionRepository collectionRepository;
    private final CollectionPublisher collectionPublisher;

    public CollectionRenameFrameController() {
        collectionRepository = new CollectionRepository();
        collectionPublisher = CollectionPublisher.getInstance();
    }

    public void save(
            CollectionTreeDto collectionTree,
            String name
    ) throws IOException {
        CollectionDto collectionDto = collectionRepository.load(collectionTree)
                .orElseThrow();

        collectionDto.setName(name);

        collectionRepository.save(
                collectionTree.getParent().getPath(),
                collectionDto
        );

        var key = new ParentCollectionKey(collectionTree.getParent().getId());
        collectionPublisher.getOnChange().publish(
                key,
                new PublisherEvent<>(EventTypeEnum.UPDATED, collectionTree.getId())
        );
    }
}
