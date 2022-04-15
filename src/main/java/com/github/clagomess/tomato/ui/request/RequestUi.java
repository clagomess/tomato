package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.ui.request.tabrequest.TabRequestUi;
import com.github.clagomess.tomato.ui.request.tabresponse.TabResponseUi;

import javax.swing.*;

public class RequestUi extends JTabbedPane {
    public RequestUi(){
        addTab("Aoba", getTabContent());
        addTab("Aoba - 2", getTabContent());
    }

    public JSplitPane getTabContent(){
        TabRequestUi tabRequestUi = new TabRequestUi();
        TabResponseUi tabResponseUi = new TabResponseUi();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabRequestUi, tabResponseUi);

        return splitPane;
    }
}
