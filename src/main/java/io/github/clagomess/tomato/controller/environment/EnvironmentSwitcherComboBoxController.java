package io.github.clagomess.tomato.controller.environment;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import io.github.clagomess.tomato.io.keystore.EnvironmentKeystore;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import io.github.clagomess.tomato.publisher.WorkspaceSessionPublisher;
import io.github.clagomess.tomato.ui.environment.EnvironmentSwitcherComboBoxInterface;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class EnvironmentSwitcherComboBoxController {
    private final EnvironmentRepository environmentRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceSessionRepository workspaceSessionRepository;
    private final WorkspacePublisher workspacePublisher;
    private final WorkspaceSessionPublisher workspaceSessionPublisher;
    private final EnvironmentPublisher environmentPublisher;
    private final EnvironmentSwitcherComboBoxInterface ui;

    public EnvironmentSwitcherComboBoxController(
            EnvironmentSwitcherComboBoxInterface ui
    ) {
        this.environmentRepository = new EnvironmentRepository();
        this.workspaceRepository = new WorkspaceRepository();
        this.workspaceSessionRepository = new WorkspaceSessionRepository();
        this.workspacePublisher = WorkspacePublisher.getInstance();
        this.workspaceSessionPublisher = WorkspaceSessionPublisher.getInstance();
        this.environmentPublisher = EnvironmentPublisher.getInstance();
        this.ui = ui;
    }

    public void addListeners() {
        environmentPublisher.getOnChange()
                .addListener(event -> ui.refreshItems());

        workspacePublisher.getOnSwitch()
                .addListener(event -> ui.refreshItems());
    }

    public void refreshEnvironmentCurrentEnvsListener() throws IOException {
        var wsSession = workspaceSessionRepository.load();
        var workspace = workspaceRepository.getDataSessionWorkspace();

        var keystore = new EnvironmentKeystore(
                workspace.getPath(),
                wsSession.getEnvironmentId()
        );

        keystore.setGetPassword(ui::getPassword);
        keystore.setGetNewPassword(ui::getNewPassword);

        environmentPublisher.getCurrentEnvs()
                .addListener(() -> {
                    try {
                        List<EnvironmentItemDto> envs = environmentRepository.getWorkspaceSessionEnvironment()
                                .map(EnvironmentDto::getEnvs)
                                .orElseThrow();

                        return keystore.loadSecret(envs);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void setWorkspaceSessionSelected(
            @Nullable EnvironmentHeadDto selected
    ) throws IOException {
        var environmentId = selected == null ? null : selected.getId();

        var session = workspaceSessionRepository.load();

        if(Objects.equals(session.getEnvironmentId(), environmentId)) return;

        session.setEnvironmentId(environmentId);

        workspaceSessionRepository.save(session);

        workspaceSessionPublisher.getOnChange()
                .publish(session);
    }

}
