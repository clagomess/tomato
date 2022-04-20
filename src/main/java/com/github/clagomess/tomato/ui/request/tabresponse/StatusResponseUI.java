package com.github.clagomess.tomato.ui.request.tabresponse;

import com.github.clagomess.tomato.dto.ResponseDto;

import javax.swing.*;
import java.awt.*;

public class StatusResponseUI extends JPanel {
    public StatusResponseUI(){
        setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    public void update(ResponseDto dto){
        removeAll();

        if(!dto.isRequestStatus()){
            add(createContainer(Color.GREEN, dto.getRequestMessage())); //@TODO: show as alert
            return;
        }

        add(createContainer(getHttpStatusColor(dto.getHttpResponse().getStatus()), String.format(
                "%s %s",
                dto.getHttpResponse().getStatus(),
                dto.getHttpResponse().getStatusReason()
        )));

        add(createContainer(Color.GRAY, String.format("%ss", dto.getHttpResponse().getRequestTime()))); //@TODO: format ms
        add(createContainer(Color.GRAY, String.format("%sKB", dto.getHttpResponse().getBodySize()))); //@TODO: format KB

        revalidate();
        repaint();
    }

    private Color getHttpStatusColor(Integer status){
        //@TODO: change color pallete
        if(status >= 100 && status <= 199) return Color.CYAN;
        if(status >= 200 && status <= 299) return Color.GREEN;
        if(status >= 300 && status <= 399) return Color.YELLOW;
        if(status >= 400 && status <= 499) return Color.ORANGE;
        return Color.RED;
    }

    private JPanel createContainer(Color color, String text){
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);

        JPanel panel = new JPanel();
        panel.setBackground(color);
        panel.add(label);

        return panel;
    }
}
