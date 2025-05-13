package io.github.clagomess.tomato.controller.collection;

import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.CollectionRepository;
import io.github.clagomess.tomato.publisher.CollectionPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.ParentCollectionKey;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;

@RequiredArgsConstructor
public class CollectionDeleteDialogController {
    private final CollectionRepository collectionRepository;
    private final CollectionPublisher collectionPublisher;

    public CollectionDeleteDialogController() {
        this.collectionRepository = new CollectionRepository();
        this.collectionPublisher = CollectionPublisher.getInstance();
    }

    public void delete(CollectionTreeDto collectionTree) throws IOException {
        collectionRepository.delete(collectionTree);

        // update source collection
        collectionPublisher.getOnChange().publish(
                new ParentCollectionKey(
                        collectionTree.getParent().getId()
                ),
                new PublisherEvent<>(DELETED, collectionTree.getId())
        );
    }
}
