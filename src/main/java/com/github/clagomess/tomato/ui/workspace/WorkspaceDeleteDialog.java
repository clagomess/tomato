package com.github.clagomess.tomato.ui.workspace;

import com.github.clagomess.tomato.controller.workspace.WorkspaceDeleteDialogController;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;

@RequiredArgsConstructor
public class WorkspaceDeleteDialog {
    private final Component parent;
    private final WorkspaceDto workspace;

    private final WorkspaceDeleteDialogController controller = new WorkspaceDeleteDialogController();

    public void showConfirmDialog(){
        int ret = JOptionPane.showConfirmDialog(
                parent,
                String.format(
                        "Are you sure you want to delete \"%s\"?",
                        workspace.getName()
                ),
                "Workspace Delete",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if(ret == JOptionPane.OK_OPTION){
            new WaitExecution(
                    parent,
                    () -> controller.delete(workspace)
            ).execute();
        }
    }
}
