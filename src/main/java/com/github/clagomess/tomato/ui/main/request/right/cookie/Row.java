package com.github.clagomess.tomato.ui.main.request.right.cookie;

import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSaveIcon;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

@Getter
@Setter
class Row extends JPanel {
    private final Container parent;
    private final Map.Entry<String, String> item;
    private final JButton btnSave = new IconButton(new BxSaveIcon(), "Set Cookie");

    public Row(
            Container parent,
            Map.Entry<String, String> item
    ){
        this.parent = parent;
        this.item = item;

        // listeners
        btnSave.addActionListener(l -> btnSaveAction());

        // layout
        setLayout(new MigLayout(
                "insets 2",
                "[][grow, fill][]"
        ));
        add(new JTextField(item.getKey()), "width 150!");
        add(new JTextField(item.getValue()), "width 150:150:100%");
        add(btnSave);
    }

    private void btnSaveAction(){
        new ExceptionDialog(parent, "Impl!");
    }
}
