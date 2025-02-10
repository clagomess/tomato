package com.github.clagomess.tomato.ui.environment.list;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import com.github.clagomess.tomato.ui.environment.EnvironmentDeleteUI;
import com.github.clagomess.tomato.ui.environment.edit.EnvironmentEditUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;

public class RowComponent extends JPanel {
    public RowComponent(
            Container parent,
            EnvironmentDto environment
    ) {
        setLayout(new MigLayout(
                "insets 2",
                "[grow,fill]0[]0[]0"
        ));

        setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#616365")));

        // edit
        var btnEdit = new IconButton(new BxEditIcon(), "Edit environment");
        btnEdit.addActionListener(l -> {
            invokeLater(() -> new EnvironmentEditUI(parent, environment));
        });

        // delete
        var btnDelete = new IconButton(new BxTrashIcon(), "Delete environment");
        btnDelete.addActionListener(l -> {
            invokeLater(() -> {
                boolean ret = new EnvironmentDeleteUI(parent, environment).showConfirmDialog();
                if(ret){
                    parent.remove(this);
                    parent.revalidate();
                    parent.repaint();
                }
            });
        });
        btnDelete.setEnabled(true);

        add(new JLabel(environment.getName()));
        add(btnEdit);
        add(btnDelete);
    }
}
