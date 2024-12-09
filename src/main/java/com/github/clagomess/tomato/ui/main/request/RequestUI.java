package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.service.RequestDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.main.request.tabrequest.TabRequestUI;
import com.github.clagomess.tomato.ui.main.request.tabresponse.TabResponseUI;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestUI extends JTabbedPane {
    private final List<String> tabsId = new ArrayList<>();

    private final RequestDataService requestDataService = RequestDataService.getInstance();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();

    public RequestUI(){
        addNewPlusTab();

        addChangeListener(e -> {
            if(getTabCount() == 1) setSelectedIndex(-1);
            if(getSelectedIndex() == getTabCount() - 1) setSelectedIndex(getTabCount() - 2);
        });

        workspacePublisher.getOnSwitch().addListener(event -> {
            tabsId.forEach(tabId -> removeTabAt(indexOfTab(tabId)));
            tabsId.clear();
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

        JButton btnPlus = new JButton("+");

        setTabComponentAt(indexOfTab("+"), btnPlus);

        btnPlus.addActionListener(l -> {
            addNewTab(null, new RequestDto());
        });
    }

    public void addNewTab(
            CollectionTreeDto.Request requestHead,
            RequestDto request
    ){
        // tab content
        TabResponseUI tabResponseUi = new TabResponseUI();
        TabRequestUI tabRequestUi = new TabRequestUI(
                requestHead,
                request,
                tabResponseUi
        );
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                tabRequestUi,
                tabResponseUi
        );

        // add new
        insertTab(request.getId(), null, splitPane, null, getTabCount() - 1);
        setupTabTitle(request);

        setSelectedIndex(getTabCount() -2);
    }

    private void setupTabTitle(RequestDto requestDto){
        var tabTitle = new TabTitleUI(requestDto);
        tabTitle.onClose(l -> {
            removeTabAt(indexOfTab(requestDto.getId()));
            tabsId.remove(requestDto.getId());
        });

        setTabComponentAt(indexOfTab(requestDto.getId()), tabTitle);
        tabsId.add(requestDto.getId());
    }
}
