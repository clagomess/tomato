package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.publisher.base.KeyPublisher;
import com.github.clagomess.tomato.publisher.base.NoKeyPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import lombok.Getter;

@Getter
public class WorkspacePublisher {
    @Getter
    private static final WorkspacePublisher instance = new WorkspacePublisher();
    private WorkspacePublisher() {}

    private final NoKeyPublisher<WorkspaceDto> onSwitch = new NoKeyPublisher<>();
    private final KeyPublisher<String, PublisherEvent<WorkspaceDto>> onChange = new KeyPublisher<>();
}
