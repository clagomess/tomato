package com.github.clagomess.tomato.ui.main.request.right.statusbadge;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.ui.ColorConstant;

import javax.swing.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class ResponseTimeBadge extends JPanel {
    public ResponseTimeBadge(ResponseDto.Response response) {
        var color = getColor(response.getRequestTime());
        setBackground(color.background());

        var label = new JLabel(formatTime(response.getRequestTime()));
        label.setForeground(color.foreground());
        add(label);
    }

    private ColorConstant.Match getColor(long duration){
        if(duration >= 60_000){
            return ColorConstant.ORANGE_MATCH;
        }else{
            return ColorConstant.GRAY_MATCH;
        }
    }

    protected String formatTime(long duration){
        if(duration <= 1000) return duration + "ms";

        return Duration.of(duration, ChronoUnit.MILLIS)
                .toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }
}
