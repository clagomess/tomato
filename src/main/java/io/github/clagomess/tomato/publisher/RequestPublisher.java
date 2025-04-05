package io.github.clagomess.tomato.publisher;

import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.publisher.base.KeyPublisher;
import io.github.clagomess.tomato.publisher.base.NoKeyPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.ParentCollectionKey;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.Getter;

@Getter
public class RequestPublisher {
    @Getter
    private static final RequestPublisher instance = new RequestPublisher();
    private RequestPublisher() {}

    private final NoKeyPublisher<PublisherEvent<RequestHeadDto>> onLoad = new NoKeyPublisher<>();
    private final KeyPublisher<TabKey, Boolean> onStaging = new KeyPublisher<>();

    private final KeyPublisher<ParentCollectionKey, PublisherEvent<RequestHeadDto>> onParentCollectionChange = new KeyPublisher<>();
    private final KeyPublisher<RequestKey, PublisherEvent<RequestHeadDto>> onChange = new KeyPublisher<>(){
        @Override
        public void publish(RequestKey key, PublisherEvent<RequestHeadDto> event) {
            super.publish(key, event);

            onParentCollectionChange.publish(
                    new ParentCollectionKey(key.getParentCollectionId()),
                    event
            );
        }
    };

    private final KeyPublisher<TabKey, KeyValueItemDto> onCookieSet = new KeyPublisher<>();
}
