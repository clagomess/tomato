package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import lombok.Getter;

@Getter
public class CollectionPublisher {
    private CollectionPublisher() {}
    private static final CollectionPublisher instance = new CollectionPublisher();
    public synchronized static CollectionPublisher getInstance(){
        return instance;
    }

    private final BasePublisher<CollectionTreeDto> onSave = new BasePublisher<>();
}
