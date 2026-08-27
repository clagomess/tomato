package io.github.clagomess.tomato.controller.workspace.edit;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.dto.data.workspace.ProxyDto;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;

import java.io.IOException;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;

public class ProxyRowPaneController {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspacePublisher workspacePublisher;

    public ProxyRowPaneController() {
        this.workspaceRepository = new WorkspaceRepository();
        this.workspacePublisher = WorkspacePublisher.getInstance();
    }

    public void delete(
            WorkspaceDto workspace,
            ProxyDto proxy
    ) throws IOException {
        workspace.getProxies().removeIf(p -> p.getId().equals(proxy.getId()));

        workspaceRepository.save(workspace);

        workspacePublisher.getOnChange().publish(
                workspace.getId(),
                new PublisherEvent<>(UPDATED, workspace)
        );
    }
}
