package io.github.clagomess.tomato.controller.workspace.edit;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.dto.data.workspace.ProxyDto;
import io.github.clagomess.tomato.io.http.SSHProxyWrapper;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.ui.workspace.edit.ProxyEditFrameInterface;

import java.io.IOException;
import java.net.URI;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;

public class ProxyEditFrameController {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspacePublisher workspacePublisher;
    private final ProxyEditFrameInterface ui;

    public ProxyEditFrameController(ProxyEditFrameInterface ui) {
        this.workspaceRepository = new WorkspaceRepository();
        this.workspacePublisher = WorkspacePublisher.getInstance();
        this.ui = ui;
    }

    public void save(
            WorkspaceDto workspace,
            ProxyDto proxy
    ) throws IOException {
        workspace.getProxies().removeIf(p -> p.getId().equals(proxy.getId()));
        workspace.getProxies().add(proxy);

        workspaceRepository.save(workspace);

        workspacePublisher.getOnChange().publish(
                workspace.getId(),
                new PublisherEvent<>(UPDATED, workspace)
        );

        ui.resetStagingMonitor();
    }

    public void test(ProxyDto proxy) throws Exception {
        new SSHProxyWrapper().wrap(
                proxy,
                URI.create("https://google.com"),
                uri -> null
        );
    }
}
