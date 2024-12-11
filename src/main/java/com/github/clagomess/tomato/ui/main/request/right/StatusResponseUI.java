package com.github.clagomess.tomato.ui.main.request.right;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.ui.ColorConstant;
import com.github.clagomess.tomato.ui.component.DialogFactory;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class StatusResponseUI extends JPanel {
    public StatusResponseUI(){
        setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    public void reset(){
        removeAll();
        revalidate();
        repaint();
    }

    public void update(ResponseDto dto){
        removeAll();

        if(!dto.isRequestStatus()){
            DialogFactory.createDialogWarning(this, dto.getRequestMessage());
            return;
        }

        add(createContainer(getHttpStatusColor(
                dto.getHttpResponse().getStatus()),
                String.format(
                    "%s %s",
                    dto.getHttpResponse().getStatus(),
                    dto.getHttpResponse().getStatusReason()
                )
        ));

        add(createContainer(
                Color.GRAY,
                formatResponseTime(dto.getHttpResponse().getRequestTime())
        ));
        add(createContainer(
                Color.GRAY,
                formatResponseBodySize(dto.getHttpResponse().getBodySize())
        ));

        revalidate();
        repaint();
    }

    private Color getHttpStatusColor(Integer status){
        if(status >= 100 && status <= 199) return ColorConstant.PURPLE;
        if(status >= 200 && status <= 299) return ColorConstant.GREEN;
        if(status >= 300 && status <= 399) return ColorConstant.YELLOW;
        if(status >= 400 && status <= 499) return ColorConstant.ORANGE;
        return ColorConstant.RED;
    }

    private JPanel createContainer(Color color, String text){
        JLabel label = new JLabel(text);
        label.setForeground(ColorConstant.FOREGROUND);

        JPanel panel = new JPanel();
        panel.setBackground(color);
        panel.add(label);

        return panel;
    }

    protected String formatResponseTime(long duration){
        if(duration <= 1000) return duration + "ms";

        return Duration.of(duration, ChronoUnit.MILLIS)
                .toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    protected String formatResponseBodySize(long size){
        if(size <= 1024) return size + "B";

        if(size <= Math.pow(1024, 2)){
            return new BigDecimal(size / 1024)
                    .setScale(2, RoundingMode.HALF_UP) + "KB";
        }

        return new BigDecimal(size / Math.pow(1024, 2))
                .setScale(2, RoundingMode.HALF_UP) + "MB";
    }
}
