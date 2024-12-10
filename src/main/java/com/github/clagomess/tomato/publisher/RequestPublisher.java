package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import lombok.Getter;

@Getter
public class RequestPublisher {
    private RequestPublisher() {}
    private static final RequestPublisher instance = new RequestPublisher();
    public synchronized static RequestPublisher getInstance(){
        return instance;
    }

    private final NoKeyPublisher<Boolean> onNew = new NoKeyPublisher<>();
    private final NoKeyPublisher<CollectionTreeDto.Request> onLoad = new NoKeyPublisher<>();
    private final KeyPublisher<String, CollectionTreeDto.Request> onSave = new KeyPublisher<>();
    private final KeyPublisher<String, Boolean> onChange = new KeyPublisher<>();
}
