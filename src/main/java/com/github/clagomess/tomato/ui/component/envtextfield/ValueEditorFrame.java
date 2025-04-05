package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import com.github.clagomess.tomato.ui.component.ColorConstant;
import com.github.clagomess.tomato.ui.component.TRSyntaxTextArea;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsMagicWandIcon;
import com.github.clagomess.tomato.ui.main.request.right.BeautifierDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Objects;

class ValueEditorFrame extends JFrame {
    private static final Icon MAGIC_WAND_ICON = new BxsMagicWandIcon();

    private final EnvTextField parent;
    private final EnvTextfieldOptions options;

    private final JComboBox<RawBodyTypeEnum> cbContentType = new JComboBox<>(
            RawBodyTypeEnum.values()
    );

    private final JButton btnBeautify = new JButton(MAGIC_WAND_ICON){{
        setToolTipText("Beautify value");
    }};

    private final TRSyntaxTextArea textArea = new TRSyntaxTextArea();

    public ValueEditorFrame(
            EnvTextField parent,
            EnvTextfieldOptions options
    ){
        this.parent = parent;
        this.options = options;

        setTitle("Value Editor");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(600, 400));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new MigLayout(
                "insets 10",
                "[grow, fill][]"
        ));

        if(options.isValueEditorShowContentTypeEdit()) {
            add(cbContentType);
            add(btnBeautify, "wrap");
        }

        var sp = TRSyntaxTextArea.createScroll(textArea);
        sp.setBorder(new MatteBorder(1, 1, 1, 1, ColorConstant.GRAY));
        add(sp, "height 100%, span 2");

        // set data
        cbContentType.setSelectedItem(options.getValueEditorSelectedRawBodyType());
        cbContentType.addActionListener(l -> cbContentTypeAction());
        btnBeautify.addActionListener(l -> btnBeautifyAction());
        cbContentTypeAction();
        textArea.setText(parent.getText());

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void cbContentTypeAction(){
        RawBodyTypeEnum type = (RawBodyTypeEnum) cbContentType.getSelectedItem();
        if(type == null) return;
        textArea.setSyntaxEditingStyle(type.getSyntaxStyle());
    }

    private void btnBeautifyAction(){
        var type = (RawBodyTypeEnum) cbContentType.getSelectedItem();
        if(type == null) return;

        new BeautifierDialog(this, type.getContentType())
                .beautify(textArea.getText(), result -> {
                    SwingUtilities.invokeLater(() -> {
                        textArea.setText(result);
                        textArea.setCaretPosition(0);
                    });
                });
    }

    @Override
    public void dispose() {
        String text = textArea.getText();

        if(!Objects.equals(parent.getText(), text)) {
            parent.setText(text);
        }

        options.getValueEditorOnDispose().run(
                (RawBodyTypeEnum) cbContentType.getSelectedItem(),
                text
        );

        super.dispose();
    }

    @FunctionalInterface
    public interface OnDisposeFI {
        void run(RawBodyTypeEnum rawBodyType, String text);
    }
}
