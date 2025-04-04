package com.github.clagomess.tomato.ui.component;

import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static com.formdev.flatlaf.FlatClientProperties.STYLE;
import static javax.swing.SwingUtilities.isEventDispatchThread;

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

        if(!isEventDispatchThread()) throw new IllegalThreadStateException();

        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(350, 100));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLayout(new MigLayout(
                "insets 10",
                "[][grow, fill]"
        ));

        txtPassword.putClientProperty(STYLE, "showRevealButton: true");
        btnSubmit.addActionListener(l -> dispose());

        add(new JLabel("Enter the password for the current environment keystore:"), "span 2, wrap");
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
