package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.ParentCollectionKey;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
public class RequestPublisherTest {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    @Test
    public void onChange_addListener(){
        requestPublisher.getOnChange().addListener(
                new RequestKey("c_a", "r_a"),
                event -> {}
        );

        requestPublisher.getOnChange().addListener(
                new RequestKey("c_a", "r_b"),
                event -> {}
        );

        Assertions.assertThat(requestPublisher.getOnParentCollectionChange().getListeners())
                .hasSize(1);
    }

    @Test
    public void onChange_removeListener(){
        var key = new RequestKey("c_a", "r_a");

        requestPublisher.getOnChange().addListener(key, event -> {});
        requestPublisher.getOnChange().removeListener(key);

        assertFalse(requestPublisher.getOnChange().containsListener(key));

        assertFalse(requestPublisher.getOnParentCollectionChange().containsListener(
                new ParentCollectionKey("c_a")
        ));
    }

    @Test
    public void onChange_publish(){
        var key = new RequestKey("c_a", "r_a");
        var count = new AtomicInteger(0);

        requestPublisher.getOnChange().addListener(
                key,
                event -> count.incrementAndGet()
        );

        requestPublisher.getOnChange().publish(
                key,
                new PublisherEvent<>(INSERTED, null)
        );

        assertEquals(2, count.get());
    }
}
