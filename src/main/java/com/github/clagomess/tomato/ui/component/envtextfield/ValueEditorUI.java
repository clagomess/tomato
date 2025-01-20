package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import com.github.clagomess.tomato.ui.component.TRSyntaxTextArea;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class ValueEditorUI extends JFrame {
    private final EnvTextField parent;

    private final JComboBox<RawBodyTypeEnum> cbContentType = new JComboBox<>(
            RawBodyTypeEnum.values()
    );

    private final TRSyntaxTextArea textArea = new TRSyntaxTextArea();

    public ValueEditorUI(EnvTextField parent){
        this.parent = parent;

        setTitle("Value Editor");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(600, 400));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));

        add(cbContentType, "wrap");
        var sp = TRSyntaxTextArea.createScroll(textArea);
        sp.setBorder(new MatteBorder(1, 1, 1, 1, Color.decode("#616365")));
        add(sp, "height 100%, width 100%");

        // set data
        cbContentType.addActionListener(l -> cbContentTypeAction());
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

    @Override
    public void dispose() {
        parent.setText(textArea.getText());
        super.dispose();
    }
}
