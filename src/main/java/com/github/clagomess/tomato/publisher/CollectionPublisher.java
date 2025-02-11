package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.publisher.base.KeyPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import lombok.Getter;

@Getter
public class CollectionPublisher {
    @Getter
    private static final CollectionPublisher instance = new CollectionPublisher();
    private CollectionPublisher() {}

    private final KeyPublisher<ParentCollectionId, PublisherEvent<String>> onChange = new KeyPublisher<>();

    public record ParentCollectionId(String parentCollectionId){}
}
