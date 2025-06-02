package io.github.clagomess.tomato.ui.component;

import io.github.clagomess.tomato.exception.TomatoException;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.formdev.flatlaf.FlatClientProperties.STYLE;
import static javax.swing.SwingUtilities.*;

public class PasswordDialog extends JDialog {
    private final JPasswordField txtPassword = new JPasswordField();
    private final JButton btnSubmit = new JButton("Submit");

    public PasswordDialog(
            @Nullable Component parent
    ) {
        super(
                parent != null ? getWindowAncestor(parent) : null,
                "Enter Password",
                Dialog.DEFAULT_MODALITY_TYPE
        );

        ComponentUtil.checkIsEventDispatchThread();

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

    public static String showInputPassword(
            @Nullable Component parent
    ) {
        if(isEventDispatchThread()){
            return new PasswordDialog(parent)
                    .showDialog();
        }

        try {
            var result = new AtomicReference<String>();
            invokeAndWait(() -> result.set(
                    new PasswordDialog(parent)
                            .showDialog()
            ));
            return result.get();
        } catch (Exception e) {
            throw new TomatoException(e);
        }
    }

    public static String showInputNewPassword(
            @Nullable Component parent
    ) {
        if(isEventDispatchThread()){
            return new NewPasswordDialog(parent)
                    .showDialog();
        }

        try {
            var result = new AtomicReference<String>();
            invokeAndWait(() -> result.set(
                    new NewPasswordDialog(parent)
                            .showDialog()
            ));
            return result.get();
        } catch (Exception e) {
            throw new TomatoException(e);
        }
    }
}
