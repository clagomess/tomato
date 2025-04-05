package io.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.publisher.base.NoKeyPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import lombok.Getter;

import java.util.List;

@Getter
public class EnvironmentPublisher {
    @Getter
    private static final EnvironmentPublisher instance = new EnvironmentPublisher();
    private EnvironmentPublisher() {}

    private final NoKeyPublisher<PublisherEvent<String>> onChange = new NoKeyPublisher<>();
    private final NoKeyPublisher<List<EnvironmentItemDto>> currentEnvs = new NoKeyPublisher<>();
}
