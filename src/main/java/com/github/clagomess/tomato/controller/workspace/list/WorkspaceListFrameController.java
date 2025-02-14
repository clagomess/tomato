package com.github.clagomess.tomato.controller.workspace.list;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.io.repository.WorkspaceRepository;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class WorkspaceListFrameController {
    private UUID listenerUuid = null;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspacePublisher workspacePublisher;

    public WorkspaceListFrameController() {
        workspaceRepository = new WorkspaceRepository();
        workspacePublisher = WorkspacePublisher.getInstance();
    }

    public void addOnChangeListener(RefreshRowsFI refreshRows) {
        listenerUuid = workspacePublisher.getOnChangeAny().addListener(event -> {
            try {
                refresh(refreshRows);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void refresh(RefreshRowsFI refreshRows) throws IOException {
        var result = workspaceRepository.list();
        refreshRows.update(result);
    }

    public void dispose() {
        if(listenerUuid == null) return;

        workspacePublisher.getOnChangeAny()
                .removeListener(listenerUuid);
    }

    @FunctionalInterface
    public interface RefreshRowsFI {
        void update(List<WorkspaceDto> item);
    }
}
