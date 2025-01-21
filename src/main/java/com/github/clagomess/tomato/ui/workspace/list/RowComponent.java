package com.github.clagomess.tomato.ui.workspace.list;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class RowComponent extends JPanel {
    public RowComponent(
            WorkspaceListUI parent,
            WorkspaceDto workspace
    ) {
        setLayout(new MigLayout(
                "insets 0",
                "[grow,fill]0[]0[]0"
        ));

        setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#616365")));

        // edit
        var btnEdit = new IconButton(new BxEditIcon(), "Edit workspace");
        btnEdit.addActionListener(l -> {
            System.out.println("action edit");
        }); // @TODO: impl. workspace edit

        // delete
        var btnDelete = new IconButton(new BxTrashIcon(), "Delete workspace");
        btnDelete.addActionListener(l -> {
            System.out.println("action deleted");
        }); // @TODO: impl. workspace delete
        btnDelete.setEnabled(true);

        add(new JLabel(workspace.getName()));
        add(btnEdit);
        add(btnDelete);
    }
}
