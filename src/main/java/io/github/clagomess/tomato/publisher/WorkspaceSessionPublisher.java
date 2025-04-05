package io.github.clagomess.tomato.publisher;

import io.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import io.github.clagomess.tomato.publisher.base.NoKeyPublisher;
import lombok.Getter;

@Getter
public class WorkspaceSessionPublisher {
    @Getter
    private static final WorkspaceSessionPublisher instance = new WorkspaceSessionPublisher();
    private WorkspaceSessionPublisher() {}

    private final NoKeyPublisher<WorkspaceSessionDto> onChange = new NoKeyPublisher<>();
}
