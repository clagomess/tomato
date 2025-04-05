package io.github.clagomess.tomato.controller.workspace;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Objects;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;

@RequiredArgsConstructor
public class WorkspaceDeleteDialogController {
    private final WorkspaceRepository workspaceRepository;

    public WorkspaceDeleteDialogController() {
        workspaceRepository = new WorkspaceRepository();
    }

    public void delete(WorkspaceDto workspace) throws IOException {
        var currentWorkspace = workspaceRepository.getDataSessionWorkspace();

        if(Objects.equals(currentWorkspace.getId(), workspace.getId())) {
            throw new RuntimeException("Is not possible to delete current workspace");
        }

        workspaceRepository.delete(workspace);

        WorkspacePublisher.getInstance()
                .getOnChange()
                .publish(
                        workspace.getId(),
                        new PublisherEvent<>(DELETED, workspace)
                );
    }
}
