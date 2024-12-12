package com.github.clagomess.tomato.ui.main.request.right.statusbadge;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.ui.ColorConstant;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ResponseSizeBadge extends JPanel {
    public ResponseSizeBadge(ResponseDto.Response response) {
        var color = getColor(response.getBodySize());
        setBackground(color.background());

        var label = new JLabel(formatSize(response.getBodySize()));
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

    protected String formatSize(long size){
        if(size <= 1024) return size + "B";

        if(size <= Math.pow(1024, 2)){
            return new BigDecimal(size / 1024)
                    .setScale(2, RoundingMode.HALF_UP) + "KB";
        }

        return new BigDecimal(size / Math.pow(1024, 2))
                .setScale(2, RoundingMode.HALF_UP) + "MB";
    }
}
