package com.github.clagomess.tomato.controller.environment;

import com.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.publisher.WorkspaceSessionPublisher;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
public class EnvironmentComboBoxController {
    private final EnvironmentRepository environmentRepository;
    private final WorkspaceSessionRepository workspaceSessionRepository;

    public EnvironmentComboBoxController() {
        this.environmentRepository = new EnvironmentRepository();
        this.workspaceSessionRepository = new WorkspaceSessionRepository();
    }

    public void addEnvironmentOnChangeListener(Runnable runnable) {
        EnvironmentPublisher.getInstance()
                .getOnChange()
                .addListener(event -> runnable.run());
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
