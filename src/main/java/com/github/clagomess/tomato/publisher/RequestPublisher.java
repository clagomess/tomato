package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.base.KeyPublisher;
import com.github.clagomess.tomato.publisher.base.NoKeyPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import lombok.Getter;

@Getter
public class RequestPublisher {
    @Getter
    private static final RequestPublisher instance = new RequestPublisher();
    private RequestPublisher() {}

    // for tab
    private final NoKeyPublisher<PublisherEvent<RequestHeadDto>> onLoad = new NoKeyPublisher<>();

    private final KeyPublisher<String, RequestHeadDto> onSave = new KeyPublisher<>();
    private final KeyPublisher<TabKey, Boolean> onStaging = new KeyPublisher<>();

    // for collection tree
    private final KeyPublisher<ParentCollectionId, RequestHeadDto> onInsert = new KeyPublisher<>();
    private final KeyPublisher<RequestKey, RequestId> onDelete = new KeyPublisher<>();
    private final KeyPublisher<RequestKey, RequestHeadDto> onUpdate = new KeyPublisher<>();

    public record RequestId(String requestId){}
    public record RequestKey(String parentCollectionId, String requestId){}
    public record ParentCollectionId(String parentCollectionId){}
}
