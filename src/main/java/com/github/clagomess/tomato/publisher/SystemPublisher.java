package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.publisher.base.NoKeyPublisher;
import lombok.Getter;

@Getter
public class SystemPublisher {
    @Getter
    private static final SystemPublisher instance = new SystemPublisher();
    private SystemPublisher() {}

    private final NoKeyPublisher<String> onClosing = new NoKeyPublisher<>();
}
