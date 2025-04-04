package com.github.clagomess.tomato.controller.environment;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import com.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import com.github.clagomess.tomato.io.keystore.EnvironmentKeystore;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.io.repository.WorkspaceRepository;
import com.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.publisher.WorkspaceSessionPublisher;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class EnvironmentComboBoxController {
    private final EnvironmentRepository environmentRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceSessionRepository workspaceSessionRepository;

    public EnvironmentComboBoxController() {
        this.environmentRepository = new EnvironmentRepository();
        this.workspaceRepository = new WorkspaceRepository();
        this.workspaceSessionRepository = new WorkspaceSessionRepository();
    }

    public void addEnvironmentOnChangeListener(Runnable runnable) {
        EnvironmentPublisher.getInstance()
                .getOnChange()
                .addListener(event -> runnable.run());
    }

    public void refreshEnvironmentCurrentEnvsListener(
            Supplier<String> getPassword,
            Supplier<String> getNewPassword
    ) throws IOException {
        var wsSession = workspaceSessionRepository.load();
        var workspace = workspaceRepository.getDataSessionWorkspace();

        var keystore = new EnvironmentKeystore(
                workspace.getPath(),
                wsSession.getEnvironmentId()
        );

        keystore.setGetPassword(getPassword);
        keystore.setGetNewPassword(getNewPassword);

        EnvironmentPublisher.getInstance()
                .getCurrentEnvs()
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

    public void addWorkspaceOnSwitchListener(Runnable runnable){
        WorkspacePublisher.getInstance()
                .getOnSwitch()
                .addListener(event -> runnable.run());
    }

    public void loadItems(
            AddItemFI addItemFI,
            Runnable onComplete
    ) throws IOException {
        var session = workspaceSessionRepository.load();
        var listEnvironment = environmentRepository.listHead();

        listEnvironment.forEach(environment -> addItemFI.set(
                environment.getId().equals(session.getEnvironmentId()),
                environment
        ));

        onComplete.run();
    }

    public void setWorkspaceSessionSelected(
            @Nullable EnvironmentHeadDto selected
    ) throws IOException {
        var environmentId = selected == null ? null : selected.getId();

        var session = workspaceSessionRepository.load();

        if(Objects.equals(session.getEnvironmentId(), environmentId)) return;

        session.setEnvironmentId(environmentId);

        workspaceSessionRepository.save(session);

        WorkspaceSessionPublisher.getInstance()
                .getOnChange()
                .publish(session);
    }

    @FunctionalInterface
    public interface AddItemFI {
        void set(boolean selected, EnvironmentHeadDto item);
    }
}
