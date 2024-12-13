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
    private final RequestStagingMonitor requestStagingMonitor;
    private final ButtonGroup bgBodyType = new ButtonGroup();

    public BodyUI(
            RequestDto requestDto,
            RequestStagingMonitor requestStagingMonitor
    ) {
        this.requestDto = requestDto;
        this.requestStagingMonitor = requestStagingMonitor;

        setLayout(new MigLayout(
                "insets 5 0 0 0",
                "[grow, fill]",
                ""
        ));

        JPanel pRadioBodyType = new JPanel(new MigLayout("insets 5 0 5 0"));

        Arrays.stream(BodyTypeEnum.values()).forEach(item -> {
            JRadioButton rbBodyType = new JRadioButton(item.getDescription());
            rbBodyType.setSelected(item == requestDto.getBody().getType());

            rbBodyType.addActionListener(l -> {
                remove(1);
                requestDto.getBody().setType(item);
                requestStagingMonitor.setActualHashCode(requestDto);
                add(getBodyType(item), "height 100%");
                revalidate();
                repaint();
            });

            bgBodyType.add(rbBodyType);
            pRadioBodyType.add(rbBodyType);
        });

        add(pRadioBodyType, "wrap");
        add(getBodyType(requestDto.getBody().getType()), "height 100%");
    }

    private Component getBodyType(BodyTypeEnum bodyType){
        return switch (bodyType) {
            case MULTIPART_FORM -> new MultiPartFormUI(requestDto); //@TODO: add requestChangeDto
            case URL_ENCODED_FORM -> new KeyValueUI(requestDto.getBody().getUrlEncodedForm()); //@TODO: add requestChangeDto
            case RAW -> new RawBodyUI(requestDto, requestStagingMonitor); //@TODO: add requestChangeDto
            case BINARY -> new BinaryUI(); //@TODO: add requestChangeDto
            default -> new NoBodyUI();
        };
    }
}
