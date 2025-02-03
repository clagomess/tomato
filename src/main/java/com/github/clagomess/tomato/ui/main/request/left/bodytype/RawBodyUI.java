package com.github.clagomess.tomato.ui.main.request.left.bodytype;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import com.github.clagomess.tomato.ui.component.TRSyntaxTextArea;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

@Slf4j
public class RawBodyUI extends JPanel {
    private final RequestDto.RawBody rawBody;
    private final RequestStagingMonitor requestStagingMonitor;

    private final JComboBox<RawBodyTypeEnum> cbContentType = new JComboBox<>(
            RawBodyTypeEnum.values()
    );
    private final TRSyntaxTextArea textArea = new TRSyntaxTextArea();

    public RawBodyUI(
            RequestDto.RawBody rawBody,
            RequestStagingMonitor requestStagingMonitor
    ){
        this.rawBody = rawBody;
        this.requestStagingMonitor = requestStagingMonitor;

        setLayout(new MigLayout(
                "insets 5 2 2 2",
                "[grow, fill]"
        ));

        cbContentType.setSelectedItem(rawBody.getType());
        cbContentType.addActionListener(l -> cbContentTypeAction());

        add(cbContentType, "wrap");
        var sp = TRSyntaxTextArea.createScroll(textArea);
        sp.setBorder(new MatteBorder(1, 1, 1, 1, Color.decode("#616365")));
        add(sp, "height 100%");

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
}
