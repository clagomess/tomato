package io.github.clagomess.tomato.ui.main.request.left;

import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.request.BodyDto;
import io.github.clagomess.tomato.enums.BodyTypeEnum;
import io.github.clagomess.tomato.publisher.DisposableListener;
import io.github.clagomess.tomato.ui.component.CharsetComboBox;
import io.github.clagomess.tomato.ui.component.ColorConstant;
import io.github.clagomess.tomato.ui.main.request.keyvalue.KeyValue;
import io.github.clagomess.tomato.ui.main.request.left.bodytype.BinaryType;
import io.github.clagomess.tomato.ui.main.request.left.bodytype.NoBodyType;
import io.github.clagomess.tomato.ui.main.request.left.bodytype.RawBodyType;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Arrays;

public class Body extends JPanel implements DisposableListener {
    private final BodyDto body;
    private final RequestStagingMonitor requestStagingMonitor;
    private final ButtonGroup bgBodyType = new ButtonGroup();
    private final CharsetComboBox charsetComboBox = new CharsetComboBox();
    private Component currentBodyType;

    public Body(
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
        pRadioBodyType.setBorder(new MatteBorder(0, 0, 1, 0, ColorConstant.GRAY));
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
            case MULTIPART_FORM -> new KeyValue<>(
                    body.getMultiPartForm(),
                    FileKeyValueItemDto.class,
                    requestStagingMonitor
            );
            case URL_ENCODED_FORM -> new KeyValue<>(
                    body.getUrlEncodedForm(),
                    ContentTypeKeyValueItemDto.class,
                    requestStagingMonitor
            );
            case RAW -> new RawBodyType(
                    body.getRaw(),
                    requestStagingMonitor
            );
            case BINARY -> new BinaryType(
                    body.getBinary(),
                    requestStagingMonitor
            );
            default -> new NoBodyType();
        };
    }

    public void dispose() {
        if(currentBodyType instanceof KeyValue<?> bodyType){
            bodyType.dispose();
        }
    }
}
