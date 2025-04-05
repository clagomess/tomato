package io.github.clagomess.tomato.ui.workspace.list;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.ui.component.ColorConstant;
import io.github.clagomess.tomato.ui.component.IconButton;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import io.github.clagomess.tomato.ui.environment.edit.EnvironmentEditFrame;
import io.github.clagomess.tomato.ui.workspace.WorkspaceDeleteDialog;
import io.github.clagomess.tomato.ui.workspace.WorkspaceRenameFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;

import static io.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;

class Row extends JPanel {
    private static final Icon EDIT_ICON = new BxEditIcon();
    private static final Icon TRASH_ICON = new BxTrashIcon();

    public Row(
            WorkspaceListFrame parent,
            WorkspaceDto workspace
    ) {
        setLayout(new MigLayout(
                "insets 2",
                "[grow,fill]0[]0[]0"
        ));

        setBorder(new MatteBorder(0, 0, 1, 0, ColorConstant.GRAY));

        // edit
        var btnEdit = new IconButton(EDIT_ICON, "Edit workspace");
        btnEdit.addActionListener(l ->
            new WaitExecution(parent, btnEdit, () -> btnEditAction(
                    parent,
                    workspace
            )).execute()
        );

        // delete
        var btnDelete = new IconButton(TRASH_ICON, "Delete workspace");
        btnDelete.addActionListener(l -> {
            new WaitExecution(parent, btnDelete, () -> new WorkspaceDeleteDialog(
                    parent,
                    workspace
            ).showConfirmDialog()).execute();
        });

        add(new JLabel(workspace.getName()));
        add(btnEdit);
        add(btnDelete);
    }

    private void btnEditAction(
            WorkspaceListFrame parent,
            WorkspaceDto workspace
    ) {
        toFrontIfExists(
                EnvironmentEditFrame.class,
                () -> new WorkspaceRenameFrame(
                        parent,
                        workspace
                )
        );
    }
}
