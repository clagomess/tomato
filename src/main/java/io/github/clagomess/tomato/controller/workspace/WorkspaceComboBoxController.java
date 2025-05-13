package io.github.clagomess.tomato.controller.workspace;

import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.ui.workspace.WorkspaceComboBoxInterface;

import java.io.IOException;

public class WorkspaceComboBoxController {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceComboBoxInterface ui;

    public WorkspaceComboBoxController(
            WorkspaceComboBoxInterface ui
    ) {
        this.workspaceRepository = new WorkspaceRepository();
        this.ui = ui;
    }

    public void loadItems() throws IOException {
        workspaceRepository.list()
                .forEach(ui::addItem);

        ui.setSelectedItem(workspaceRepository.getDataSessionWorkspace());
    }
}
