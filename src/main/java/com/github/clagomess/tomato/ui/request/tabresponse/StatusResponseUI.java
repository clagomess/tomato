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
            add(createContainer(Color.GREEN, dto.getRequestMessage()));
            return;
        }

        add(createContainer(Color.GREEN, String.format(
                "%s %s",
                dto.getHttpResponse().getStatus(),
                dto.getHttpResponse().getStatusReason()
        ))); //@TODO: needs change collor

        add(createContainer(Color.GRAY, String.format("%ss", dto.getHttpResponse().getRequestTime()))); //@TODO: format ms
        add(createContainer(Color.GRAY, String.format("%sKB", dto.getHttpResponse().getBodySize()))); //@TODO: format KB

        revalidate();
        repaint();
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
