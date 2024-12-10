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

    // for tab
    private final NoKeyPublisher<Boolean> onOpenNew = new NoKeyPublisher<>();
    private final NoKeyPublisher<CollectionTreeDto.Request> onLoad = new NoKeyPublisher<>();
    private final KeyPublisher<String, CollectionTreeDto.Request> onSave = new KeyPublisher<>();
    private final KeyPublisher<String, Boolean> onChange = new KeyPublisher<>();

    // for collection tree
    private final KeyPublisher<ParentCollectionId, CollectionTreeDto.Request> onInsert = new KeyPublisher<>();
    private final KeyPublisher<RequestKey, CollectionTreeDto.Request> onUpdate = new KeyPublisher<>();

    public record RequestKey(String parentCollectionId, String requestId){}
    public record ParentCollectionId(String parentCollectionId){}
}
