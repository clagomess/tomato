package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import lombok.Getter;

@Getter
public class WorkspacePublisher {
    private WorkspacePublisher() {}
    private static final WorkspacePublisher instance = new WorkspacePublisher();
    public synchronized static WorkspacePublisher getInstance(){
        return instance;
    }

    private final NoKeyPublisher<WorkspaceDto> onSwitch = new NoKeyPublisher<>();
}
