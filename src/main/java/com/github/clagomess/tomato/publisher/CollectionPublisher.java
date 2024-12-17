package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import lombok.Getter;

@Getter
public class CollectionPublisher {
    @Getter
    private static final CollectionPublisher instance = new CollectionPublisher();
    private CollectionPublisher() {}

    private final KeyPublisher<ParentCollectionId, CollectionTreeDto> onSave = new KeyPublisher<>();

    public record ParentCollectionId(String parentCollectionId){}
}
