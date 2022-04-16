package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.ui.request.tabrequest.TabRequestUi;
import com.github.clagomess.tomato.ui.request.tabresponse.TabResponseUi;

import javax.swing.*;

public class RequestUi extends JTabbedPane {
    private final String ADD_TAB_TITLE = "+";

    public RequestUi(){
        addTab("Aoba", getTabContent());
        addTab("Aoba - 2", getTabContent());
        addTab(ADD_TAB_TITLE, new JPanel());

        addChangeListener(e -> {
            if(getSelectedIndex() != indexOfTab(ADD_TAB_TITLE)) return;

            removeTabAt(getSelectedIndex());
            addTab("New Request", getTabContent());
            addTab("+", new JPanel());
        });
    }

    public JSplitPane getTabContent(){
        TabRequestUi tabRequestUi = new TabRequestUi();
        TabResponseUi tabResponseUi = new TabResponseUi();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabRequestUi, tabResponseUi);

        return splitPane;
    }
}
