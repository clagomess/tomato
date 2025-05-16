package io.github.clagomess.tomato.controller.environment;

import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import io.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import io.github.clagomess.tomato.ui.environment.EnvironmentComboBoxInterface;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class EnvironmentComboBoxController {
    private final WorkspaceSessionRepository workspaceSessionRepository;
    private final EnvironmentRepository environmentRepository;
    private final EnvironmentComboBoxInterface ui;

    public EnvironmentComboBoxController(EnvironmentComboBoxInterface ui) {
        this.workspaceSessionRepository = new WorkspaceSessionRepository();
        this.environmentRepository = new EnvironmentRepository();
        this.ui = ui;
    }

    public void loadItems() throws IOException {
        var session = workspaceSessionRepository.load();
        var listEnvironment = environmentRepository.listHead();

        listEnvironment.forEach(environment -> {
            ui.addItem(environment);

            if(environment.getId().equals(session.getEnvironmentId())){
                ui.setSelectedItem(environment);
            }
        });
    }
}
