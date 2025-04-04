package com.github.clagomess.tomato.ui.component;

import com.github.clagomess.tomato.io.keystore.PasswordEntropyCalculator;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

import static com.formdev.flatlaf.FlatClientProperties.STYLE;
import static com.github.clagomess.tomato.ui.component.ColorConstant.*;

public class NewPasswordDialog extends JDialog {
    private static final String DEFAULT_STYLE = "showRevealButton: true";
    private static final String ERROR_STYLE = "outline: error; showRevealButton: true";

    private final JPasswordField txtPassword = new JPasswordField();
    private final JPasswordField txtConfirmPassword = new JPasswordField();
    private final JProgressBar strength = new JProgressBar();
    private final JButton btnSubmit = new JButton("Submit");

    private final PasswordEntropyCalculator entropyCalculator = new PasswordEntropyCalculator();

    public NewPasswordDialog(
            @Nullable Component parent
    ) {
        super(
                parent != null ? SwingUtilities.getWindowAncestor(parent) : null,
                "New Password",
                Dialog.DEFAULT_MODALITY_TYPE
        );

        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(350, 100));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLayout(new MigLayout(
                "insets 10",
                "[][grow, fill]"
        ));

        // config
        strength.setMaximum(100);
        strength.setMinimum(0);
        strength.setStringPainted(true);
        btnSubmit.setEnabled(false);
        btnSubmit.addActionListener(l -> dispose());

        add(new JLabel("Create a new keystore for current environment:"), "span 2, wrap");
        add(new JLabel("Password:"));
        add(txtPassword, "wrap");
        add(new JLabel("Confirm password:"));
        add(txtConfirmPassword, "wrap");
        add(new JLabel("Strength:"));
        add(strength, "wrap");
        add(btnSubmit, "span 2, align right");

        // listeners
        refreshStrength();
        addPasswordChangeListener();
        addConfirmPasswordChangeListener();

        getRootPane().setDefaultButton(btnSubmit);

        pack();
        setLocationRelativeTo(parent);
    }

    private void addPasswordChangeListener() {
        txtPassword.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                txtPassword.putClientProperty(STYLE, DEFAULT_STYLE);
                refreshStrength();
                checkPasswordsEquals();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(e.getDocument().getLength() == 0){
                    txtPassword.putClientProperty(STYLE, ERROR_STYLE);
                }

                refreshStrength();
                checkPasswordsEquals();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
    }

    private void addConfirmPasswordChangeListener() {
        txtConfirmPassword.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkPasswordsEquals();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkPasswordsEquals();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
    }

    private void checkPasswordsEquals() {
        var password = new String(txtPassword.getPassword());
        var confirmPassword = new String(txtConfirmPassword.getPassword());

        if (StringUtils.equals(password, confirmPassword)) {
            txtConfirmPassword.putClientProperty(STYLE, DEFAULT_STYLE);
            btnSubmit.setEnabled(true);
        }else{
            txtConfirmPassword.putClientProperty(STYLE, ERROR_STYLE);
            btnSubmit.setEnabled(false);
        }
    }

    private void refreshStrength(){
        var result = entropyCalculator.calculateEntropy(new String(txtPassword.getPassword()));

        switch (result.getStrength()){
            case LOW:
                strength.setForeground(RED_MATCH.background());
                strength.setValue(33);
                break;
            case MEDIUM:
                strength.setForeground(YELLOW_MATCH.background());
                strength.setValue(66);
                break;
            case HIGH:
                strength.setForeground(GREEN_MATCH.background());
                strength.setValue(100);
                break;
        }

        if(result.getBits() == 0){
            strength.setValue(0);
        }

        strength.setString(result.getBits() + " bits");
    }

    public String showDialog(){
        setVisible(true);
        return new String(txtPassword.getPassword());
    }
}
