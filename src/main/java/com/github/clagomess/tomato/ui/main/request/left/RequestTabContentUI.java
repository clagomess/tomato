package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.keyvalue.KeyValueUI;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

@Getter
@Setter
public class RequestTabContentUI extends JPanel {
    private final URIParamUI uriParamUI;

    public RequestTabContentUI(
            TabKey tabKey,
            RequestDto requestDto,
            RequestStagingMonitor requestStagingMonitor
    ){
        // layout definitions
        setLayout(new MigLayout("insets 5 0 0 5", "[grow,fill]", ""));

        uriParamUI = new URIParamUI(tabKey, requestDto, requestStagingMonitor);

        JTabbedPane tpRequest = new JTabbedPane();
        //@TODO: add count
        tpRequest.addTab("Params", uriParamUI);
        tpRequest.addTab("Body", new BodyUI(requestDto.getBody(), requestStagingMonitor));
        //@TODO: add count
        tpRequest.addTab("Headers", new KeyValueUI(requestDto.getHeaders(), requestStagingMonitor));
        //@TODO: add count
        tpRequest.addTab("Cookies", new KeyValueUI(requestDto.getCookies(), requestStagingMonitor));
        add(tpRequest, "span, height 100%");
    }

    public void dispose(){
        uriParamUI.dispose();
    }
}
