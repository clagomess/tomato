package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxListPlusIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class EnvTextField extends JPanel {
    private final EnvDocumentListener envDocumentListener;
    private final JButton btnEnvView;
    private final JTextPane textPane;

    public EnvTextField() {
        this.btnEnvView = new JButton(new BxListPlusIcon());
        this.btnEnvView.setToolTipText("View Injected Environment");
        this.btnEnvView.addActionListener(e -> btnEnvViewAction());
        this.btnEnvView.setEnabled(false);

        this.textPane = new JTextPane();

        this.envDocumentListener = new EnvDocumentListener(this.textPane.getStyledDocument());
        this.textPane.getStyledDocument().addDocumentListener(this.envDocumentListener);
        this.envDocumentListener.getOnChangeList().add(content -> setBtnEnvViewEnabledOrDisabled());

        var noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(textPane);
        var spTextPane = new JScrollPane(noWrapPanel);

        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[][grow, fill]"
        ));
        add(btnEnvView);
        add(spTextPane, "height 100%");
    }

    public void addOnChange(EnvTextFieldOnChangeFI value){
        envDocumentListener.getOnChangeList().add(value);
    }

    private void setBtnEnvViewEnabledOrDisabled(){
        btnEnvView.setEnabled(
                textPane.isEnabled() &&
                !envDocumentListener.getEnvMap()
                        .getInjected()
                        .isEmpty()
        );
    }

    private void btnEnvViewAction(){
        SwingUtilities.invokeLater(() -> new ViewInjectedEnvironmentUI(
                this,
                envDocumentListener.getEnvMap()
        ));
    }

    public void setText(String text){
        textPane.setText(text);
    }

    public void dispose(){
        envDocumentListener.dispose();
    }

    public void setEnabled(boolean enabled){
        textPane.setEnabled(enabled);
        setBtnEnvViewEnabledOrDisabled();
    }
}
