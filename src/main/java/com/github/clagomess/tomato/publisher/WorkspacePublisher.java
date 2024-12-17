package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import lombok.Getter;

@Getter
public class WorkspacePublisher {
    @Getter
    private static final WorkspacePublisher instance = new WorkspacePublisher();
    private WorkspacePublisher() {}

    private final NoKeyPublisher<WorkspaceDto> onSwitch = new NoKeyPublisher<>();
}
