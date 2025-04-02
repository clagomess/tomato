package com.github.clagomess.tomato.ui.component;

import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class PasswordDialog extends JDialog {
    private final JPasswordField txtPassword = new JPasswordField();
    private final JButton btnSubmit = new JButton("Submit");

    public PasswordDialog(
            @Nullable Component parent
    ) {
        super(
                parent != null ? SwingUtilities.getWindowAncestor(parent) : null,
                "Enter Password",
                Dialog.DEFAULT_MODALITY_TYPE
        );

        setMinimumSize(new Dimension(350, 100));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout(
                "",
                "[][grow, fill]"
        ));

        add(new JLabel("Password:"));
        add(txtPassword, "wrap");
        add(btnSubmit, "span 2, align right");

        getRootPane().setDefaultButton(btnSubmit);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
