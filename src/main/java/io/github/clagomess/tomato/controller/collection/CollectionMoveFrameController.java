package io.github.clagomess.tomato.controller.collection;

import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.CollectionRepository;
import io.github.clagomess.tomato.publisher.CollectionPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.ParentCollectionKey;

import java.io.IOException;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;
import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;

public class CollectionMoveFrameController {
    private final CollectionRepository collectionRepository;
    private final CollectionPublisher collectionPublisher;

    public CollectionMoveFrameController() {
        this.collectionRepository = new CollectionRepository();
        this.collectionPublisher = CollectionPublisher.getInstance();
    }

    public void move(
            CollectionTreeDto source,
            CollectionTreeDto target
    ) throws IOException {
        if(target == null) throw new RuntimeException("Destination not selected");

        collectionRepository.move(source, target);

        // update source collection
        collectionPublisher.getOnChange().publish(
                new ParentCollectionKey(
                        source.getParent().getId()
                ),
                new PublisherEvent<>(DELETED, source.getId())
        );

        // update dest collection
        collectionPublisher.getOnChange().publish(
                new ParentCollectionKey(
                        target.getId()
                ),
                new PublisherEvent<>(INSERTED, source.getId())
        );
    }
}
