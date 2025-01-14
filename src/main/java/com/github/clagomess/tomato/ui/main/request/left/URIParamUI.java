package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.ui.main.request.left.bodytype.keyvalue.KeyValueUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;

public class URIParamUI extends JPanel {
    public URIParamUI(
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
        pQueryParams.add(new KeyValueUI(new ArrayList<>(), requestStagingMonitor)); // @TODO: impl

        // Path Variables
        var pPathVariables = new JPanel(new MigLayout(
                "insets 0 5 5 5",
                "[grow, fill]"
        ));
        pPathVariables.setBorder(BorderFactory.createTitledBorder("Path Variables"));
        pPathVariables.add(new KeyValueUI(new ArrayList<>(), requestStagingMonitor)); // @TODO: impl

        add(pQueryParams, "height 100%, wrap");
        add(pPathVariables, "height 100%");
    }
}
