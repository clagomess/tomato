package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.BinaryUI;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.NoBodyUI;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.RawBodyUI;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.keyvalue.KeyValueUI;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.multipartform.MultiPartFormUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Arrays;

public class BodyUI extends JPanel {
    private final RequestDto.Body body;
    private final RequestStagingMonitor requestStagingMonitor;
    private final ButtonGroup bgBodyType = new ButtonGroup();

    public BodyUI(
            RequestDto.Body body,
            RequestStagingMonitor requestStagingMonitor
    ) {
        this.body = body;
        this.requestStagingMonitor = requestStagingMonitor;

        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[grow, fill]",
                ""
        ));

        JPanel pRadioBodyType = new JPanel(new MigLayout(
                "insets 5 0 5 0",
                "[]",
                ""
        ));
        pRadioBodyType.setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#616365")));

        Arrays.stream(BodyTypeEnum.values()).forEach(item -> {
            JRadioButton rbBodyType = new JRadioButton(item.getDescription());
            rbBodyType.setSelected(item == body.getType());

            rbBodyType.addActionListener(l -> {
                remove(1);
                body.setType(item);
                requestStagingMonitor.update();
                add(getBodyType(), "height 100%");
                revalidate();
                repaint();
            });

            bgBodyType.add(rbBodyType);
            pRadioBodyType.add(rbBodyType);
        });

        add(pRadioBodyType, "wrap 0");
        add(getBodyType(), "height 100%");
    }

    private Component getBodyType(){
        return switch (body.getType()) {
            case MULTIPART_FORM -> new MultiPartFormUI(
                    body.getMultiPartForm(),
                    requestStagingMonitor
            );
            case URL_ENCODED_FORM -> new KeyValueUI(
                    body.getUrlEncodedForm(),
                    requestStagingMonitor
            );
            case RAW -> new RawBodyUI(
                    body.getRaw(),
                    requestStagingMonitor
            );
            case BINARY -> new BinaryUI(
                    body.getBinary(),
                    requestStagingMonitor
            );
            default -> new NoBodyUI();
        };
    }
}
