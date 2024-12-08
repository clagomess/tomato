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

    private final BasePublisher<CollectionTreeDto.Request> onLoad = new BasePublisher<>();
    private final BasePublisher<CollectionTreeDto.Request> onSave = new BasePublisher<>();
}
