package io.github.clagomess.tomato.controller.workspace;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.io.repository.DataSessionRepository;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@RequiredArgsConstructor
public class WorkspaceSwitchFrameController {
    private final DataSessionRepository dataSessionRepository;

    public WorkspaceSwitchFrameController() {
        this.dataSessionRepository = new DataSessionRepository();
    }

    public void switchWorkspace(
            @NotNull WorkspaceDto selected
    ) throws IOException {
        WorkspacePublisher.getInstance()
                .getOnBeforeSwitch()
                .publish("switching");

        var session = dataSessionRepository.load();
        session.setWorkspaceId(selected.getId());
        dataSessionRepository.save(session);

        WorkspacePublisher.getInstance()
                .getOnSwitch()
                .publish(selected);
    }
}
