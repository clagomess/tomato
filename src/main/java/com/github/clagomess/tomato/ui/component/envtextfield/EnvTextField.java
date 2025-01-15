package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxListPlusIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

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

        this.textPane = new JTextPane(); // @TODO: disable render lne break

        this.envDocumentListener = new EnvDocumentListener(this.textPane.getStyledDocument());
        this.textPane.getStyledDocument().addDocumentListener(this.envDocumentListener);
        this.envDocumentListener.getOnChangeList().add(content -> setBtnEnvViewEnabledOrDisabled());

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

    // @TODO: MERGE
    public void addOnChange(EnvTextFieldOnChangeFI value){
        envDocumentListener.getOnChangeList().add(value);
    }

    public List<EnvTextFieldOnChangeFI> getOnChangeList(){
        return envDocumentListener.getOnChangeList();
    }
    // <<<<

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

    public void setEnabled(boolean enabled){
        textPane.setEnabled(enabled);

        if(enabled){
            setBtnEnvViewEnabledOrDisabled();
        }else{
            btnEnvView.setEnabled(false);
        }
    }
}
