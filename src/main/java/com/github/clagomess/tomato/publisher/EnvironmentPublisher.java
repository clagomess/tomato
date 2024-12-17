package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import lombok.Getter;

@Getter
public class EnvironmentPublisher {
    private EnvironmentPublisher() {}
    private static final EnvironmentPublisher instance = new EnvironmentPublisher();
    public synchronized static EnvironmentPublisher getInstance(){
        return instance;
    }

    // for tab
    private final NoKeyPublisher<String> onInsert = new NoKeyPublisher<>();
    private final KeyPublisher<String, EnvironmentDto> onSave = new KeyPublisher<>();
}
