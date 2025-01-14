package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RequestTabPaneUI extends JTabbedPane {
    private final List<Tab> tabs = new ArrayList<>();

    private final RequestRepository requestRepository = new RequestRepository();
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
            new WaitExecution(() -> {
                addNewTab(null, new RequestDto());
            }).execute();
        });

        requestPublisher.getOnLoad().addListener(event -> {
            new WaitExecution(() -> {
                requestRepository.load(event)
                        .ifPresent(item -> addNewTab(event, item));
            }).execute();
        });
    }

    private void addNewPlusTab(){
        addTab("+", new JPanel());
        setSelectedIndex(-1);

        JButton btnPlus = new JButton(new BxPlusIcon());
        btnPlus.setToolTipText("Add a new request");

        setTabComponentAt(indexOfTab("+"), btnPlus);

        btnPlus.addActionListener(l -> {
            new WaitExecution(() -> {
                addNewTab(null, new RequestDto());
            }).execute();
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
        // @TODO: check if unsaved before remove
        var tabId = tab.tabKey().getUuid().toString();
        removeTabAt(indexOfTab(tabId));
        tab.tabTitleUI().dispose();
        tab.tabContent().dispose();
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
