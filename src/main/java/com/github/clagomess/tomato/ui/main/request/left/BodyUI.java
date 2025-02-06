package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.request.BodyDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.ui.component.CharsetComboBox;
import com.github.clagomess.tomato.ui.main.request.keyvalue.KeyValueUI;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.BinaryUI;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.NoBodyUI;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.RawBodyUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Arrays;

public class BodyUI extends JPanel {
    private final BodyDto body;
    private final RequestStagingMonitor requestStagingMonitor;
    private final ButtonGroup bgBodyType = new ButtonGroup();
    private final CharsetComboBox charsetComboBox = new CharsetComboBox();
    private Component currentBodyType;

    public BodyUI(
            BodyDto body,
            RequestStagingMonitor requestStagingMonitor
    ) {
        this.body = body;
        this.requestStagingMonitor = requestStagingMonitor;

        // layouy
        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[grow, fill]"
        ));

        JPanel pRadioBodyType = new JPanel(new MigLayout(
                "insets 5 2 5 2",
                "[]"
        ));
        pRadioBodyType.setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#616365")));
        pRadioBodyType.add(charsetComboBox);

        Arrays.stream(BodyTypeEnum.values()).forEach(item -> {
            JRadioButton rbBodyType = new JRadioButton(item.getDescription());
            rbBodyType.setSelected(item == body.getType());

            rbBodyType.addActionListener(l -> {
                dispose();
                remove(1);
                body.setType(item);
                requestStagingMonitor.update();

                currentBodyType = getBodyType();
                add(currentBodyType, "height 100%");
                revalidate();
                repaint();
            });

            bgBodyType.add(rbBodyType);
            pRadioBodyType.add(rbBodyType);
        });

        charsetComboBox.setSelectedItem(body.getCharset());
        charsetComboBox.addActionListener(l -> {
            body.setCharset(charsetComboBox.getSelectedItem());
            requestStagingMonitor.update();
        });

        add(pRadioBodyType, "wrap 0");
        currentBodyType = getBodyType();
        add(currentBodyType, "height 100%");
    }

    private Component getBodyType(){
        return switch (body.getType()) {
            case MULTIPART_FORM -> new KeyValueUI<>(
                    body.getMultiPartForm(),
                    FileKeyValueItemDto.class,
                    requestStagingMonitor
            );
            case URL_ENCODED_FORM -> new KeyValueUI<>(
                    body.getUrlEncodedForm(),
                    ContentTypeKeyValueItemDto.class,
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

    public void dispose() {
        if(currentBodyType instanceof KeyValueUI<?> bodyType){
            bodyType.dispose();
        }
    }
}
