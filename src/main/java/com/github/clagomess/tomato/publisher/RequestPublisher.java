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

    private final NoKeyPublisher<CollectionTreeDto.Request> onLoad = new NoKeyPublisher<>();
    private final NoKeyPublisher<CollectionTreeDto.Request> onSave = new NoKeyPublisher<>();

    public record ChangeEvent(String id, boolean change){};
    private final NoKeyPublisher<ChangeEvent> onChange = new NoKeyPublisher<>();
}
