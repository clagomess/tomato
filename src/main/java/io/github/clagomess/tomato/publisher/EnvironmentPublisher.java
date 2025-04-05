package io.github.clagomess.tomato.publisher;

import io.github.clagomess.tomato.publisher.base.NoKeyPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import lombok.Getter;

@Getter
public class EnvironmentPublisher {
    @Getter
    private static final EnvironmentPublisher instance = new EnvironmentPublisher();
    private EnvironmentPublisher() {}

    private final NoKeyPublisher<PublisherEvent<String>> onChange = new NoKeyPublisher<>();
}
