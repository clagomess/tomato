package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.base.KeyPublisher;
import com.github.clagomess.tomato.publisher.base.NoKeyPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.ParentCollectionKey;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.Getter;

import java.util.UUID;

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
        public UUID addListener(RequestKey key, OnChangeFI<PublisherEvent<RequestHeadDto>> runnable) {
            var parentKey = new ParentCollectionKey(key.getParentCollectionId());

            if(!onParentCollectionChange.containsListener(parentKey)) {
                onParentCollectionChange.addListener(
                        new ParentCollectionKey(key.getParentCollectionId()),
                        runnable
                );
            }

            return super.addListener(key, runnable);
        }

        @Override
        public void removeListener(RequestKey key) {
            onParentCollectionChange.removeListener(new ParentCollectionKey(key.getParentCollectionId()));
            super.removeListener(key);
        }

        @Override
        public void publish(RequestKey key, PublisherEvent<RequestHeadDto> event) {
            super.publish(key, event);

            onParentCollectionChange.publish(
                    new ParentCollectionKey(key.getParentCollectionId()),
                    event
            );
        }
    };
}
