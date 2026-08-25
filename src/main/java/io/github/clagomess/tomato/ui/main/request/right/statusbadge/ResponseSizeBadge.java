package io.github.clagomess.tomato.ui.main.request.right.statusbadge;

import io.github.clagomess.tomato.dto.ResponseDto;
import io.github.clagomess.tomato.ui.component.ColorConstant;

import javax.swing.*;

import static io.github.clagomess.tomato.util.FileUtils.humanReadableByteCountBinary;

public class ResponseSizeBadge extends JPanel {
    public ResponseSizeBadge(ResponseDto.Response response) {
        var color = getColor(response.getBodySize());
        setBackground(color.background());

        var label = new JLabel(humanReadableByteCountBinary(response.getBodySize()));
        label.setForeground(color.foreground());
        add(label);
    }

    private ColorConstant.Match getColor(long duration){
        if(duration > 1_048_576){
            return ColorConstant.ORANGE_MATCH;
        }else{
            return ColorConstant.GRAY_MATCH;
        }
    }
}
