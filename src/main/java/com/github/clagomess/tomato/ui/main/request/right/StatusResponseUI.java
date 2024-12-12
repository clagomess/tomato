package com.github.clagomess.tomato.ui.main.request.right;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.main.request.right.statusbadge.*;

import javax.swing.*;
import java.awt.*;

public class StatusResponseUI extends JPanel {
    public StatusResponseUI(){
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new InfoBadge("Waiting response"));
    }

    public void reset(){
        removeAll();
        add(new InfoBadge("Waiting response"));
        revalidate();
        repaint();
    }

    public void update(ResponseDto dto){
        removeAll();

        if(!dto.isRequestStatus()){
            DialogFactory.createDialogWarning(this, dto.getRequestMessage());
            return;
        }

        add(new HttpStatusBadge(dto.getHttpResponse()));
        add(new ResponseTimeBadge(dto.getHttpResponse()));
        add(new ResponseSizeBadge(dto.getHttpResponse()));
        add(new CharsetBadge(dto.getHttpResponse()));

        revalidate();
        repaint();
    }
}
