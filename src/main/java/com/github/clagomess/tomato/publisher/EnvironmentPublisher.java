package com.github.clagomess.tomato.publisher;

import lombok.Getter;

@Getter
public class EnvironmentPublisher {
    @Getter
    private static final EnvironmentPublisher instance = new EnvironmentPublisher();
    private EnvironmentPublisher() {}

    // for tab
    private final NoKeyPublisher<String> onChange = new NoKeyPublisher<>();
}
