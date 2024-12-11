package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.service.RequestDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestTabPaneUI extends JTabbedPane {
    private final List<Tab> tabs = new ArrayList<>();

    private final RequestDataService requestDataService = RequestDataService.getInstance();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();

    public RequestTabPaneUI(){
        addNewPlusTab();

        addChangeListener(e -> {
            if(getTabCount() == 1) setSelectedIndex(-1);
            if(getSelectedIndex() == getTabCount() - 1) setSelectedIndex(getTabCount() - 2);
        });

        workspacePublisher.getOnSwitch().addListener(event -> {
            new ArrayList<>(tabs).forEach(this::removeTab);
        });

        requestPublisher.getOnOpenNew().addListener(event -> {
            addNewTab(null, new RequestDto());
        });

        requestPublisher.getOnLoad().addListener(event -> {
            try {
                requestDataService.load(event)
                        .ifPresent(item -> addNewTab(event, item));
            } catch (IOException e) {
                DialogFactory.createDialogException(this, e);
            }
        });
    }

    private void addNewPlusTab(){
        addTab("+", new JPanel());
        setSelectedIndex(-1);

        JButton btnPlus = new JButton(new BxPlusIcon());

        setTabComponentAt(indexOfTab("+"), btnPlus);

        btnPlus.addActionListener(l -> {
            addNewTab(null, new RequestDto());
        });
    }

    public void addNewTab(
            RequestHeadDto requestHeadDto,
            RequestDto requestDto
    ){
        // add content
        var requestSplitPaneUI = new RequestSplitPaneUI(requestHeadDto, requestDto);
        var tabId = requestSplitPaneUI.getKey().getUuid().toString();

        insertTab(
                tabId,
                null,
                requestSplitPaneUI,
                null,
                getTabCount() - 1
        );

        // setup tab title
        var tabTitle = new TabTitleUI(requestSplitPaneUI.getKey(), requestDto);
        tabTitle.onClose(l -> removeTab(requestSplitPaneUI.getKey()));

        setTabComponentAt(indexOfTab(tabId), tabTitle);
        tabs.add(new Tab(
                requestSplitPaneUI.getKey(),
                tabTitle,
                requestSplitPaneUI
        ));

        // select
        setSelectedIndex(getTabCount() -2);
    }

    private void removeTab(Tab tab){
        var tabId = tab.tabKey().getUuid().toString();
        removeTabAt(indexOfTab(tabId));
        tab.tabTitleUI().dispose();
        tab.tabContent().getRequestContent().dispose();
        tabs.remove(tab);
    }

    private void removeTab(TabKey tabKey){
        tabs.stream()
                .filter(tab -> tab.tabKey().equals(tabKey))
                .findFirst()
                .ifPresent(this::removeTab);
    }

    private record Tab(
            TabKey tabKey,
            TabTitleUI tabTitleUI,
            RequestSplitPaneUI tabContent
    ) { }
}
