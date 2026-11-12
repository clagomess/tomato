package io.github.clagomess.tomato.controller.request.left;

import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
public class ConfigPanelController {
    private UUID listenerUuid = null;

    private final WorkspaceRepository workspaceRepository;
    private final WorkspacePublisher workspacePublisher;

    public ConfigPanelController() {
        workspaceRepository = new WorkspaceRepository();
        workspacePublisher = WorkspacePublisher.getInstance();
    }

    public void addOnChangeListener(
            Runnable runnable
    ) throws IOException {
        var workspace = workspaceRepository.getDataSessionWorkspace();
        listenerUuid = workspacePublisher.getOnChange()
                .addListener(workspace.getId(), event -> runnable.run());
    }

    public void dispose() {
        if(listenerUuid == null) return;

        workspacePublisher.getOnChange()
                .removeListener(listenerUuid);
    }
}
