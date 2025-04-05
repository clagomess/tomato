package io.github.clagomess.tomato.publisher;

import io.github.clagomess.tomato.publisher.base.KeyPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.ParentCollectionKey;
import lombok.Getter;

@Getter
public class CollectionPublisher {
    @Getter
    private static final CollectionPublisher instance = new CollectionPublisher();
    private CollectionPublisher() {}

    private final KeyPublisher<ParentCollectionKey, PublisherEvent<String>> onChange = new KeyPublisher<>();
}
