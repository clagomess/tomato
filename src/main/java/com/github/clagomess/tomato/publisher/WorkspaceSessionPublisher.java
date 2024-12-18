package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import lombok.Getter;

@Getter
public class WorkspaceSessionPublisher {
    @Getter
    private static final WorkspaceSessionPublisher instance = new WorkspaceSessionPublisher();
    private WorkspaceSessionPublisher() {}

    private final NoKeyPublisher<WorkspaceSessionDto> onSave = new NoKeyPublisher<>();
}
