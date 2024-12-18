package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.keyvalue.KeyValueUI;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

@Getter
@Setter
public class RequestTabContentUI extends JPanel {
    public RequestTabContentUI(
            RequestDto requestDto,
            RequestStagingMonitor requestStagingMonitor
    ){
        // layout definitions
        setLayout(new MigLayout("insets 5 0 0 5", "[grow,fill]", ""));

        JTabbedPane tpRequest = new JTabbedPane();
        tpRequest.addTab("Body", new BodyUI(requestDto.getBody(), requestStagingMonitor));
        //@TODO: add count
        tpRequest.addTab("Header", new KeyValueUI(requestDto.getHeaders(), requestStagingMonitor));
        //@TODO: add count
        tpRequest.addTab("Cookie", new KeyValueUI(requestDto.getCookies(), requestStagingMonitor));
        add(tpRequest, "span, height 100%");
    }
}
