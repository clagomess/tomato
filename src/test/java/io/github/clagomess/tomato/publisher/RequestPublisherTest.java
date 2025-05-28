package io.github.clagomess.tomato.publisher;

import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class RequestPublisherTest {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    private String collectionId;
    private String requestId;

    @BeforeEach
    void setUp() {
        collectionId = RandomStringUtils.secure().nextAlphanumeric(8);
        requestId = RandomStringUtils.secure().nextAlphanumeric(8);
    }

    @Test
    void onChange_publish(){
        var key = new RequestKey(collectionId, requestId);
        var count = new AtomicInteger(0);

        requestPublisher.getOnChange().addListener(
                key,
                event -> count.incrementAndGet()
        );

        requestPublisher.getOnChange().publish(
                key,
                new PublisherEvent<>(INSERTED, null)
        );

        assertEquals(1, count.get());
    }
}
