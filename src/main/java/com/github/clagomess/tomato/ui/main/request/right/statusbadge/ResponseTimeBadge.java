package com.github.clagomess.tomato.ui.main.request.right.statusbadge;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.ui.ColorConstant;
import lombok.Getter;

import javax.swing.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class ResponseTimeBadge extends JPanel {
    private final JLabel label = new JLabel("0ms");

    @Getter
    private long duration = 0;

    public ResponseTimeBadge(ResponseDto.Response response) {
        this(response.getRequestTime());
    }

    public ResponseTimeBadge(long duration) {
        add(label);
        tick(duration);
    }

    public void tick(long duration){
        this.duration = duration;

        var color = getColor(duration);
        setBackground(color.background());

        label.setText(formatTime(duration));
        label.setForeground(color.foreground());
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
