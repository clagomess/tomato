package com.github.clagomess.tomato.ui.request.tabresponse;

import com.github.clagomess.tomato.constant.ColorConstant;
import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.factory.DialogFactory;

import javax.swing.*;
import java.awt.*;

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
}
