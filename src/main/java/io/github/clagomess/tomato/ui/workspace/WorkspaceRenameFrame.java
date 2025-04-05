package io.github.clagomess.tomato.ui.workspace;

import io.github.clagomess.tomato.controller.workspace.WorkspaceRenameFrameController;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.ui.component.NameFrame;
import io.github.clagomess.tomato.ui.component.WaitExecution;

import java.awt.*;

public class WorkspaceRenameFrame extends NameFrame {
    private final WorkspaceRenameFrameController controller = new WorkspaceRenameFrameController();

    public WorkspaceRenameFrame(
            Component parent,
            WorkspaceDto workspace
    ) {
        super(parent);
        setTitle("Workspace Rename");

        txtName.setText(workspace.getName());

        btnSave.addActionListener(l -> new WaitExecution(this, btnSave, () -> {
            workspace.setName(txtName.getText());
            controller.save(workspace);

            setVisible(false);
            dispose();
        }).execute());
    }
}
