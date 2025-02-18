package com.github.clagomess.tomato.publisher.base;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class BasePublisherTest {
    @Test
    public void removeListener_concurrent(){
        var publisher = new BasePublisher<String, String>(){};
        List<UUID> uuids = new LinkedList<>();

        IntStream.range(0, 100).forEach(i -> {
            var listener = new BasePublisher.Listener<String, String>(
                    RandomStringUtils.secure().nextAlphanumeric(8),
                    (event) -> {}
            );

            publisher.getListeners().add(listener);
            uuids.add(listener.getUuid());
        });

        uuids.parallelStream()
                .forEach(publisher::removeListener);

        Assertions.assertThat(publisher.getListeners())
                .isEmpty();
    }
}
