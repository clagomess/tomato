package io.github.clagomess.tomato.publisher;

import io.github.clagomess.tomato.dto.data.TomatoID;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.publisher.base.NoKeyPublisher;
import io.github.clagomess.tomato.publisher.base.NoKeyRequestPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import lombok.Getter;

import java.util.List;

@Getter
public class EnvironmentPublisher {
    @Getter
    private static final EnvironmentPublisher instance = new EnvironmentPublisher();
    private EnvironmentPublisher() {}

    private final NoKeyPublisher<PublisherEvent<TomatoID>> onChange = new NoKeyPublisher<>();
    private final NoKeyRequestPublisher<List<EnvironmentItemDto>, CollectionTreeDto> currentEnvs = new NoKeyRequestPublisher<>();
}
