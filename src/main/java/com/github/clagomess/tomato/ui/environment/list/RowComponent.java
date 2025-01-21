package com.github.clagomess.tomato.ui.environment.list;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import com.github.clagomess.tomato.ui.environment.edit.EnvironmentEditUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;

public class RowComponent extends JPanel {
    public RowComponent(
            EnvironmentListUI parent,
            EnvironmentDto environment
    ) {
        setLayout(new MigLayout(
                "insets 0",
                "[grow,fill]0[]0[]0"
        ));

        setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#616365")));

        // edit
        var btnEdit = new JButton(new BxEditIcon());
        btnEdit.setToolTipText("Edit environment");
        btnEdit.setBorderPainted(false);
        btnEdit.setBackground(null);
        btnEdit.setFocusable(false);
        btnEdit.setMargin(new Insets(0,0,0,0));
        btnEdit.addActionListener(l -> {
            invokeLater(() -> new EnvironmentEditUI(parent, environment));
        });

        // delete
        var btnDelete = new JButton(new BxTrashIcon());
        btnDelete.setToolTipText("Delete environment");
        btnDelete.setBorderPainted(false);
        btnDelete.setBackground(null);
        btnDelete.setFocusable(false);
        btnDelete.setMargin(new Insets(0,0,0,0));
        btnDelete.addActionListener(l -> {
            System.out.println("action deleted");
        }); // @TODO: impl. environment delete
        btnDelete.setEnabled(true);

        add(new JLabel(environment.getName()));
        add(btnEdit);
        add(btnDelete);
    }
}
