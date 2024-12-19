package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxListPlusIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;

public class EnvTextField extends JPanel {
    private final EnvDocumentListener envDocumentListener;
    private final JButton btnEnvView;
    private final JTextPane textPane;

    public EnvTextField() {
        this.btnEnvView = new JButton(new BxListPlusIcon());
        this.btnEnvView.setToolTipText("View Injected Environment");
        this.btnEnvView.addActionListener(e -> btnEnvViewAction());

        this.textPane = new JTextPane();

        this.envDocumentListener = new EnvDocumentListener(this.textPane.getStyledDocument());
        this.textPane.getStyledDocument().addDocumentListener(this.envDocumentListener);

        var spTextPane = new JScrollPane(textPane);
        spTextPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        spTextPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[][grow, fill]"
        ));
        add(btnEnvView);
        add(spTextPane, "height 100%");
    }

    public void addOnChange(EnvTextFieldOnChangeFI value){
        envDocumentListener.getOnChangeList().add(value);
        envDocumentListener.getOnChangeList().add(content -> setBtnEnvViewEnabledOrDisabled());
    }

    private void setBtnEnvViewEnabledOrDisabled(){
        btnEnvView.setEnabled(
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
}
