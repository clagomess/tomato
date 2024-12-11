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
import java.awt.*;
import java.util.Arrays;

public class BodyUI extends JPanel {
    private final RequestDto requestDto;
    private final ButtonGroup bgBodyType = new ButtonGroup();

    public BodyUI(RequestDto requestDto) {
        this.requestDto = requestDto;

        setLayout(new MigLayout(
                "insets 5 0 0 0",
                "[grow, fill]",
                ""
        ));

        JPanel pRadioBodyType = new JPanel(new MigLayout("insets 5 0 5 0"));

        Arrays.stream(BodyTypeEnum.values()).forEach(item -> {
            JRadioButton rbBodyType = new JRadioButton(item.getDescription());
            rbBodyType.setSelected(item == requestDto.getBody().getBodyType());

            rbBodyType.addActionListener(l -> {
                remove(1);
                requestDto.getBody().setBodyType(item);
                add(getBodyType(item), "height 100%");
                revalidate();
                repaint();
            });

            bgBodyType.add(rbBodyType);
            pRadioBodyType.add(rbBodyType);
        });

        add(pRadioBodyType, "wrap");
        add(getBodyType(requestDto.getBody().getBodyType()), "height 100%");
    }

    private Component getBodyType(BodyTypeEnum bodyType){
        return switch (bodyType) {
            case MULTIPART_FORM -> new MultiPartFormUI(requestDto);
            case URL_ENCODED_FORM -> new KeyValueUI(requestDto.getBody().getUrlEncodedForm());
            case RAW -> new RawBodyUI();
            case BINARY -> new BinaryUI();
            default -> new NoBodyUI();
        };
    }
}
