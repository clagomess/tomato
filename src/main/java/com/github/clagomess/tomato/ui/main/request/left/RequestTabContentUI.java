package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
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

        // titles
        var paramsTabTitle = new TabTitleUI(
                "Params",
                !requestDto.getUrlParam().getQuery().isEmpty() ||
                !requestDto.getUrlParam().getPath().isEmpty()
        );
        var bodyTabTabTitle = new TabTitleUI(
                "Body",
                requestDto.getBody().getType() != BodyTypeEnum.NO_BODY
        );
        var headersTabTitle = new TabTitleUI(
                "Headers",
                !requestDto.getHeaders().isEmpty()
        );
        var cookiesTabTitle = new TabTitleUI(
                "Cookies",
                !requestDto.getCookies().isEmpty()
        );

        JTabbedPane tpRequest = new JTabbedPane();

        tpRequest.addTab(paramsTabTitle.getTitle(), new URIParamUI(
                requestDto.getUrlParam(),
                requestStagingMonitor
        ));

        tpRequest.addTab(bodyTabTabTitle.getTitle(), new BodyUI(
                requestDto.getBody(),
                requestStagingMonitor
        ));

        tpRequest.addTab(headersTabTitle.getTitle(), new KeyValueUI(
                requestDto.getHeaders(),
                requestStagingMonitor
        ));

        tpRequest.addTab(cookiesTabTitle.getTitle(), new KeyValueUI(
                requestDto.getCookies(),
                requestStagingMonitor
        ));

        add(tpRequest, "span, height 100%");

        // init tab-title
        tpRequest.setTabComponentAt(tpRequest.indexOfTab(paramsTabTitle.getTitle()), paramsTabTitle);
        tpRequest.setTabComponentAt(tpRequest.indexOfTab(bodyTabTabTitle.getTitle()), bodyTabTabTitle);
        tpRequest.setTabComponentAt(tpRequest.indexOfTab(headersTabTitle.getTitle()), headersTabTitle);
        tpRequest.setTabComponentAt(tpRequest.indexOfTab(cookiesTabTitle.getTitle()), cookiesTabTitle);

        // @TODO: update tab title when modify content
    }
}
