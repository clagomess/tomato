package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import lombok.Getter;

@Getter
public class EnvironmentPublisher {
    @Getter
    private static final EnvironmentPublisher instance = new EnvironmentPublisher();
    private EnvironmentPublisher() {}

    // for tab
    private final NoKeyPublisher<String> onInsert = new NoKeyPublisher<>();
    private final KeyPublisher<String, EnvironmentDto> onSave = new KeyPublisher<>();
}
