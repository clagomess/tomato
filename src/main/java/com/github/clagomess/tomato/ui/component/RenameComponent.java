package com.github.clagomess.tomato.ui.component;

import com.github.clagomess.tomato.factory.IconFactory;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class RenameComponent extends JFrame {
    protected final JButton btnSave = new JButton("Save");
    protected final JTextField txtName = new JTextField();

    public RenameComponent(Component parent){
        setTitle("Rename");
        setIconImage(IconFactory.ICON_FAVICON.getImage());
        setMinimumSize(new Dimension(300, 100));
        setResizable(false);

        JPanel panel = new JPanel(new MigLayout());
        panel.add(new JLabel("Name"), "wrap");
        panel.add(txtName, "width 100%, wrap");
        panel.add(btnSave, "align right");

        add(panel);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
