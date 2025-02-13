package com.github.clagomess.tomato.controller.workspace;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.io.repository.WorkspaceRepository;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;

@RequiredArgsConstructor
public class WorkspaceRenameFrameController {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspacePublisher workspacePublisher;

    public WorkspaceRenameFrameController() {
        this.workspaceRepository = new WorkspaceRepository();
        this.workspacePublisher = WorkspacePublisher.getInstance();
    }

    public void save(WorkspaceDto workspace) throws IOException {
        workspaceRepository.save(workspace);

        workspacePublisher.getOnChange().publish(
                workspace.getId(),
                new PublisherEvent<>(UPDATED, workspace)
        );
    }
}
