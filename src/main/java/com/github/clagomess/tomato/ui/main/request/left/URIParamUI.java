package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.io.http.UrlBuilder;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.keyvalue.KeyValueUI;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

@Slf4j
public class URIParamUI extends JPanel {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final TabKey tabKey;
    private final RequestDto requestDto;
    private final KeyValueUI queryUI;
    private final KeyValueUI pathUI;

    public URIParamUI(
            TabKey tabKey,
            RequestDto requestDto,
            RequestStagingMonitor requestStagingMonitor
    ) {
        this.tabKey = tabKey;
        this.requestDto = requestDto;

        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[grow, fill]"
        ));

        // Query Params
        var pQueryParams = new JPanel(new MigLayout(
                "insets 0 5 5 5",
                "[grow, fill]"
        ));
        pQueryParams.setBorder(BorderFactory.createTitledBorder("Query Params"));
        queryUI = new KeyValueUI(requestDto.getUrlParam().getQuery(), requestStagingMonitor);
        queryUI.setOnChange(item -> onChange());
        pQueryParams.add(queryUI);

        // Path Variables
        var pPathVariables = new JPanel(new MigLayout(
                "insets 0 5 5 5",
                "[grow, fill]"
        ));
        pPathVariables.setBorder(BorderFactory.createTitledBorder("Path Variables"));
        pathUI = new KeyValueUI(requestDto.getUrlParam().getPath(), requestStagingMonitor);
        pathUI.setOnChange(item -> onChange());
        pPathVariables.add(pathUI);

        add(pQueryParams, "height 100%, wrap");
        add(pPathVariables, "height 100%");

        // listeners
        requestPublisher.getOnUrlChange().addListener(tabKey, value -> {
            updateQueryParam(requestDto.getUrlParam().getQuery(), value);
        });
    }

    protected void updateQueryParam(List<RequestDto.KeyValueItem> query, String value){
        try {
            new UrlBuilder(value).updateQueryParam(query);
            queryUI.refresh();
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    private void onChange(){
        var url = new UrlBuilder(requestDto.getUrl())
                .recreateUrl(requestDto.getUrlParam().getQuery());

        requestPublisher.getOnUrlParamChange().publish(tabKey, url);
    }

    public void dispose(){
        requestPublisher.getOnUrlChange().removeListener(tabKey);
    }
}
