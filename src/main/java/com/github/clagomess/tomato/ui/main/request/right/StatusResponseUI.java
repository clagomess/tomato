package com.github.clagomess.tomato.ui.main.request.right;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.main.request.right.statusbadge.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class StatusResponseUI extends JPanel {
    public StatusResponseUI(){
        setLayout(new MigLayout("insets 0 0 0 0"));
        add(new InfoBadge("Waiting new request"));
    }

    private Timer requestTimer;
    public void reset(){
        removeAll();

        var timeBadge = new ResponseTimeBadge(0);
        add(timeBadge);

        revalidate();
        repaint();

        requestTimer = new Timer(100, l -> {
            timeBadge.tick(timeBadge.getDuration() + 100);
        });
        requestTimer.start();
    }

    public void update(ResponseDto dto){
        requestTimer.stop();
        removeAll();

        if(!dto.isRequestStatus()){
            DialogFactory.createDialogWarning(this, dto.getRequestMessage());
            add(new ErrorBadge("ERROR"));
        }else {
            add(new HttpStatusBadge(dto.getHttpResponse()));
            add(new ResponseTimeBadge(dto.getHttpResponse()));
            add(new ResponseSizeBadge(dto.getHttpResponse()));
            add(new CharsetBadge(dto.getHttpResponse()));
        }

        revalidate();
        repaint();
    }
}
