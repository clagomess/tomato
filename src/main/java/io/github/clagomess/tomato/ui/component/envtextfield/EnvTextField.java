package io.github.clagomess.tomato.ui.component.envtextfield;

import io.github.clagomess.tomato.ui.component.IconButton;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxLinkExternalIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxListPlusIcon;
import io.github.clagomess.tomato.ui.component.undoabletextcomponent.UndoableTextPane;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import static io.github.clagomess.tomato.ui.component.PreventDefaultFrame.disposeIfExists;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class EnvTextField extends JPanel {
    private static final Icon LIST_PLUS_ICON = new BxListPlusIcon();
    private static final Icon LINK_EXTERNAL_ICON = new BxLinkExternalIcon();

    private final EnvDocumentListener envDocumentListener;
    private final JButton btnEnvView;
    private final JButton btnExpand;
    private final UndoableTextPane textPane;

    public EnvTextField(EnvTextfieldOptions options) {
        this.btnEnvView = new IconButton(
                LIST_PLUS_ICON,
                "View Injected Environment"
        );
        this.btnEnvView.addActionListener(e -> btnEnvViewAction());
        this.btnEnvView.setEnabled(false);

        this.btnExpand = new IconButton(
                LINK_EXTERNAL_ICON,
                "Expand to value editor"
        );
        this.btnExpand.addActionListener(e -> btnExpandAction(options));

        this.textPane = new UndoableTextPane();

        this.envDocumentListener = new EnvDocumentListener(this.textPane.getStyledDocument());
        this.textPane.getStyledDocument().addDocumentListener(this.envDocumentListener);
        this.envDocumentListener.getOnChangeList().add(content -> setBtnEnvViewEnabledOrDisabled());

        var spTextPane = new JScrollPane(textPane);
        spTextPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        spTextPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

        setLayout(new MigLayout(
                "insets 2",
                "0[]0[]2[grow, fill]"
        ));
        add(btnEnvView);
        add(btnExpand);

        int spTextPaneHeight = spTextPane.getPreferredSize().height - 4;
        add(spTextPane, String.format(
                "height %s:%s:%s",
                spTextPaneHeight,
                spTextPaneHeight,
                spTextPaneHeight
        ));
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
        disposeIfExists(
                ViewInjectedEnvironmentFrame.class,
                () -> new ViewInjectedEnvironmentFrame(
                        this,
                        envDocumentListener.getEnvMap()
                )
        );
    }

    private void btnExpandAction(EnvTextfieldOptions options){
        disposeIfExists(
                ValueEditorFrame.class,
                () -> new WaitExecution(
                    this,
                    () -> new ValueEditorFrame(this, options)
                ).execute()
        );
    }

    public void setText(String text){
        textPane.setText(text);
    }

    public String getText(){
        return textPane.getText();
    }

    public void dispose(){
        envDocumentListener.dispose();
    }

    public void setEnabled(boolean enabled){
        btnExpand.setEnabled(enabled);
        textPane.setEnabled(enabled);
        setBtnEnvViewEnabledOrDisabled();
    }
}
