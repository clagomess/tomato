package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.keyvalue.KeyValueUI;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

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
        var queryParamsTabTitle = new TabTitleUI(
                "Query Params",
                !requestDto.getUrlParam().getQuery().isEmpty()
        );
        var pathVariablesTabTitle = new TabTitleUI(
                "Path Variables",
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

        tpRequest.addTab(queryParamsTabTitle.getTitle(), new KeyValueUI(
                requestDto.getUrlParam().getQuery(),
                requestStagingMonitor
        ));

        tpRequest.addTab(pathVariablesTabTitle.getTitle(), new KeyValueUI(
                requestDto.getUrlParam().getPath(),
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
        tpRequest.setTabComponentAt(0, queryParamsTabTitle);
        tpRequest.setTabComponentAt(1, pathVariablesTabTitle);
        tpRequest.setTabComponentAt(2, bodyTabTabTitle);
        tpRequest.setTabComponentAt(3, headersTabTitle);
        tpRequest.setTabComponentAt(4, cookiesTabTitle);

        // select tab with content
        List<TabTitleUI> tabs = List.of(
                queryParamsTabTitle, pathVariablesTabTitle, bodyTabTabTitle,
                headersTabTitle, cookiesTabTitle
        );

        for (var i = 0; i < tabs.size(); i++) {
            if(tabs.get(i).isHasContent()){
                tpRequest.setSelectedIndex(i);
                break;
            }
        }

        // @TODO: update tab title when modify content
    }
}
