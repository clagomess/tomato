package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.keyvalue.KeyValueUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class URIParamUI extends JPanel {
    private final KeyValueUI queryUI;
    private final KeyValueUI pathUI;

    public URIParamUI(
            RequestDto requestDto,
            RequestStagingMonitor requestStagingMonitor
    ) {
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
        pQueryParams.add(queryUI);

        // Path Variables
        var pPathVariables = new JPanel(new MigLayout(
                "insets 0 5 5 5",
                "[grow, fill]"
        ));
        pPathVariables.setBorder(BorderFactory.createTitledBorder("Path Variables"));
        pathUI = new KeyValueUI(requestDto.getUrlParam().getPath(), requestStagingMonitor);
        pPathVariables.add(pathUI);

        add(pQueryParams, "height 100%, wrap");
        add(pPathVariables, "height 100%");
    }
}
