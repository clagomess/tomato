package io.github.clagomess.tomato.ui.main.request.left.bodytype;

import io.github.clagomess.tomato.dto.data.request.RawBodyDto;
import io.github.clagomess.tomato.enums.RawBodyTypeEnum;
import io.github.clagomess.tomato.ui.component.ColorConstant;
import io.github.clagomess.tomato.ui.component.TRSyntaxTextArea;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsMagicWandIcon;
import io.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import io.github.clagomess.tomato.ui.main.request.right.BeautifierDialog;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;

@Slf4j
public class RawBodyType extends JPanel {
    private static final Icon MAGIC_WAND_ICON = new BxsMagicWandIcon();

    private final RawBodyDto rawBody;
    private final RequestStagingMonitor requestStagingMonitor;

    private final JComboBox<RawBodyTypeEnum> cbContentType = new JComboBox<>(
            RawBodyTypeEnum.values()
    );
    private final JButton btnBeautify = new JButton(MAGIC_WAND_ICON){{
        setToolTipText("Beautify body");
    }};
    private final TRSyntaxTextArea textArea = new TRSyntaxTextArea();

    public RawBodyType(
            RawBodyDto rawBody,
            RequestStagingMonitor requestStagingMonitor
    ){
        this.rawBody = rawBody;
        this.requestStagingMonitor = requestStagingMonitor;

        setLayout(new MigLayout(
                "insets 5 2 2 2",
                "[grow, fill][]"
        ));

        cbContentType.setSelectedItem(rawBody.getType());
        cbContentType.addActionListener(l -> cbContentTypeAction());

        btnBeautify.addActionListener(l -> btnBeautifyAction());

        add(cbContentType);
        add(btnBeautify, "wrap");
        var sp = TRSyntaxTextArea.createScroll(textArea);
        sp.setBorder(new MatteBorder(1, 1, 1, 1, ColorConstant.GRAY));
        add(sp, "height 100%, span 2");

        textArea.setSyntaxEditingStyle(rawBody.getType().getSyntaxStyle());
        textArea.setText(rawBody.getRaw());
        textArea.addOnChange(value -> {
            rawBody.setRaw(value);
            requestStagingMonitor.update();
        });
    }

    private void cbContentTypeAction(){
        RawBodyTypeEnum type = (RawBodyTypeEnum) cbContentType.getSelectedItem();
        if(type == null) return;
        textArea.setSyntaxEditingStyle(type.getSyntaxStyle());
        rawBody.setType(type);
        requestStagingMonitor.update();
    }

    private void btnBeautifyAction(){
        new BeautifierDialog(this, rawBody.getType().getContentType())
                .beautify(rawBody.getRaw(), result -> {
                    SwingUtilities.invokeLater(() -> {
                        textArea.setText(result);
                        textArea.setCaretPosition(0);
                    });
                });
    }
}
