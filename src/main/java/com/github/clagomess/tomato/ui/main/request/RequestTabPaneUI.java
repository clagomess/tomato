package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.publisher.base.EventTypeEnum;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestTabPaneUI extends JTabbedPane {
    @Getter
    private final List<Tab> tabs = new ArrayList<>();

    private final RequestRepository requestRepository = new RequestRepository();

    public RequestTabPaneUI(){
        addNewPlusTab();

        addChangeListener(e -> {
            if(getTabCount() == 1) setSelectedIndex(-1);
            if(getSelectedIndex() == getTabCount() - 1) setSelectedIndex(getTabCount() - 2);
        });

        WorkspacePublisher.getInstance()
                .getOnSwitch()
                .addListener(event -> {
                    new ArrayList<>(tabs).forEach(this::removeTab);
                });

        RequestPublisher.getInstance()
                .getOnLoad()
                .addListener(e -> new WaitExecution(() -> {
                    if(e.getType().equals(EventTypeEnum.NEW)){
                        addNewTabForNewRequest();
                    }else{
                        addNewTabForRequest(e.getEvent());
                    }
                }).execute());
    }

    private void addNewPlusTab(){
        addTab("+", new JPanel());
        setSelectedIndex(-1);

        JButton btnPlus = new JButton(new BxPlusIcon());
        btnPlus.setToolTipText("Add a new request");
        btnPlus.setBorderPainted(false);
        btnPlus.setContentAreaFilled(false);
        btnPlus.setFocusable(false);
        btnPlus.setMargin(new Insets(0,0,0,0));
        btnPlus.addActionListener(l ->
                new WaitExecution(this::addNewTabForNewRequest)
                        .execute()
        );

        setTabComponentAt(indexOfTab("+"), btnPlus);
    }

    protected void addNewTabForNewRequest(){
        addNewTab(null, new RequestDto());
    }

    protected void addNewTabForRequest(
            @NotNull RequestHeadDto requestHead
    ) throws IOException {
        var request = requestRepository.load(requestHead).orElseThrow();
        addNewTab(requestHead, request);
    }

    protected void addNewTab(
            @Nullable RequestHeadDto requestHeadDto,
            @NotNull RequestDto requestDto
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
        var tabTitle = new TabTitleUI(
                this,
                requestSplitPaneUI.getKey(),
                requestHeadDto,
                requestDto
        );
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

    protected void removeTab(Tab tab){
        // @TODO: check if unsaved before remove
        var tabId = tab.tabKey().getUuid().toString();
        removeTabAt(indexOfTab(tabId));
        tab.tabTitleUI().dispose();
        tab.tabContent().dispose();
        tabs.remove(tab);
    }

    protected void removeTab(TabKey tabKey){
        tabs.stream()
                .filter(tab -> tab.tabKey().equals(tabKey))
                .findFirst()
                .ifPresent(this::removeTab);
    }

    protected record Tab(
            TabKey tabKey,
            TabTitleUI tabTitleUI,
            RequestSplitPaneUI tabContent
    ) { }
}
