package com.github.clagomess.tomato.ui.environment.list;

import com.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import com.github.clagomess.tomato.ui.component.ColorConstant;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import com.github.clagomess.tomato.ui.environment.EnvironmentDeleteUI;
import com.github.clagomess.tomato.ui.environment.edit.EnvironmentEditUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class RowComponent extends JPanel {
    public RowComponent(
            Container parent,
            EnvironmentHeadDto environment
    ) {
        setLayout(new MigLayout(
                "insets 2",
                "[grow,fill]0[]0[]0"
        ));

        setBorder(new MatteBorder(0, 0, 1, 0, ColorConstant.GRAY));

        // edit
        var btnEdit = new IconButton(new BxEditIcon(), "Edit environment");
        btnEdit.addActionListener(l -> {
            new WaitExecution(parent, btnEdit, () -> new EnvironmentEditUI(
                    parent,
                    environment.getId()
            )).execute();
        });

        // delete
        var btnDelete = new IconButton(new BxTrashIcon(), "Delete environment");
        btnDelete.addActionListener(l -> {
            new WaitExecution(parent, btnEdit, () -> {
                new EnvironmentDeleteUI(
                        parent,
                        environment.getId()
                ).showConfirmDialog();
            }).execute();
        });
        btnDelete.setEnabled(true);

        add(new JLabel(environment.getName()));
        add(btnEdit);
        add(btnDelete);
    }
}
