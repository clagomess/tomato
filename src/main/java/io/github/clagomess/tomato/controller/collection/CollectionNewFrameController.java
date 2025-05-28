package io.github.clagomess.tomato.controller.collection;

import io.github.clagomess.tomato.dto.data.CollectionDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.exception.TomatoException;
import io.github.clagomess.tomato.io.repository.CollectionRepository;
import io.github.clagomess.tomato.publisher.CollectionPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.ParentCollectionKey;

import java.io.IOException;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;

public class CollectionNewFrameController {
    private final CollectionRepository collectionRepository;
    private final CollectionPublisher collectionPublisher;

    public CollectionNewFrameController() {
        collectionRepository = new CollectionRepository();
        collectionPublisher = CollectionPublisher.getInstance();
    }

    public void save(
            CollectionTreeDto parent,
            String name
    ) throws IOException {
        if(parent == null) throw new TomatoException("Parent is null");

        CollectionDto dto = new CollectionDto(name);
        collectionRepository.save(
                parent.getPath(),
                dto
        );

        var key = new ParentCollectionKey(parent.getId());
        collectionPublisher.getOnChange().publish(
                key,
                new PublisherEvent<>(INSERTED, dto.getId())
        );
    }
}
