package com.github.clagomess.tomato.controller.workspace;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.io.repository.DataSessionRepository;
import com.github.clagomess.tomato.io.repository.WorkspaceRepository;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class WorkspaceNewFrameController {
    private final DataSessionRepository dataSessionRepository;
    private final WorkspaceRepository workspaceRepository;

    public WorkspaceNewFrameController() {
        this.dataSessionRepository = new DataSessionRepository();
        this.workspaceRepository = new WorkspaceRepository();
    }

    public void save(
            String name
    ) throws IOException {
        WorkspacePublisher.getInstance()
                .getOnBeforeSwitch()
                .publish("switching");

        WorkspaceDto workspace = new WorkspaceDto();
        workspace.setName(name);
        workspaceRepository.save(workspace);

        var session = dataSessionRepository.load();
        session.setWorkspaceId(workspace.getId());
        dataSessionRepository.save(session);

        WorkspacePublisher.getInstance()
                .getOnSwitch()
                .publish(workspace);
    }
}
