package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.publisher.base.NoKeyPublisher;
import lombok.Getter;

@Getter
public class EnvironmentPublisher {
    @Getter
    private static final EnvironmentPublisher instance = new EnvironmentPublisher();
    private EnvironmentPublisher() {}

    // for tab
    private final NoKeyPublisher<String> onChange = new NoKeyPublisher<>();
}
