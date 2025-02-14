package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.controller.main.request.RequestTabbedPaneController;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RequestTabbedPane extends JTabbedPane {
    @Getter
    private final List<Tab> tabs = new LinkedList<>();
    private final RequestTabbedPaneController controller = new RequestTabbedPaneController();

    public RequestTabbedPane(){
        addNewPlusTab();

        addChangeListener(e -> {
            if(getTabCount() == 1) setSelectedIndex(-1);
            if(getSelectedIndex() == getTabCount() - 1) setSelectedIndex(getTabCount() - 2);
        });

        controller.addWorkspaceOnSwitchListener(() -> {
            new ArrayList<>(tabs).forEach(this::removeTab);
        });

        controller.addRequestOnLoadListener((requestHead, request) -> new WaitExecution(() -> {
            addNewTab(requestHead, request);
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
                new WaitExecution(() -> addNewTab(null, new RequestDto()))
                        .execute()
        );

        setTabComponentAt(indexOfTab("+"), btnPlus);
    }

    protected void addNewTab(
            @Nullable RequestHeadDto requestHeadDto,
            @NotNull RequestDto requestDto
    ){
        // add content
        var requestSplitPane = new RequestSplitPane(requestHeadDto, requestDto);
        var tabId = requestSplitPane.getKey().getUuid().toString();

        insertTab(
                tabId,
                null,
                requestSplitPane,
                null,
                getTabCount() - 1
        );

        // setup tab title
        var tabTitle = new TabTitle(
                this,
                requestSplitPane,
                requestHeadDto,
                requestDto,
                l -> removeTab(requestSplitPane.getKey())
        );

        setTabComponentAt(indexOfTab(tabId), tabTitle);
        tabs.add(new Tab(
                requestSplitPane.getKey(),
                tabTitle,
                requestSplitPane
        ));

        // select
        setSelectedIndex(getTabCount() -2);
    }

    protected boolean isSafeToRemoveTab(Tab tab){
        if(!tab.tabContent().getRequestStagingMonitor().isDiferent()) return true;

        int ret = JOptionPane.showConfirmDialog(
                this,
                "Unsaved changes detected. Do you want to close without save?",
                "Closing Tab",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        return ret == JOptionPane.OK_OPTION;
    }

    protected void removeTab(Tab tab){
        if(!isSafeToRemoveTab(tab)) return;

        var tabId = tab.tabKey().getUuid().toString();
        removeTabAt(indexOfTab(tabId));
        tab.tabTitle().dispose();
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
            TabTitle tabTitle,
            RequestSplitPane tabContent
    ) { }
}
