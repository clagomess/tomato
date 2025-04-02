package com.github.clagomess.tomato.ui.component;

import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static com.formdev.flatlaf.FlatClientProperties.STYLE;

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

        txtPassword.putClientProperty(STYLE, "showRevealButton: true");
        btnSubmit.addActionListener(l -> dispose());

        add(new JLabel("Password:"));
        add(txtPassword, "wrap");
        add(btnSubmit, "span 2, align right");

        getRootPane().setDefaultButton(btnSubmit);

        pack();
        setLocationRelativeTo(parent);
    }

    public String showDialog(){
        setVisible(true);
        return new String(txtPassword.getPassword());
    }
}
