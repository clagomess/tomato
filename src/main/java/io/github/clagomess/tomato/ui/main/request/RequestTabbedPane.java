package io.github.clagomess.tomato.ui.main.request;

import io.github.clagomess.tomato.controller.main.request.RequestTabbedPaneController;
import io.github.clagomess.tomato.dto.RequestTabSnapshotDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.ui.component.ExceptionDialog;
import io.github.clagomess.tomato.ui.component.LoadingPane;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;
import io.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isEventDispatchThread;

public class RequestTabbedPane extends JTabbedPane {
    private static final Icon PLUS_ICON = new BxPlusIcon();

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
            new ArrayList<>(tabs).forEach(tab -> removeTab(tab, true));
            loadRequestFromSession();
        });

        controller.addRequestOnLoadListener((requestHead, request) -> invokeLater(() ->
            addNewTab(requestHead, request)
        ));

        controller.addSaveRequestsSnapshotListener(() ->
            tabs.stream().map(item -> new RequestTabSnapshotDto(
                    item.tabContent().getRequestStagingMonitor().isDiferent(),
                    item.tabContent().getRequestHeadDto(),
                    item.tabContent().getRequestDto()
            )).toList()
        );

        loadRequestFromSession();
    }

    private void loadRequestFromSession(){
        ForkJoinPool.commonPool().submit(() -> {
            try {
                controller.loadRequestFromSession((requestHead, request) ->
                        invokeLater(() -> addNewTab(requestHead, request))
                );
            } catch (IOException e) {
                new ExceptionDialog(this, e);
            }
        });
    }

    private void addNewPlusTab(){
        addTab("+", new JPanel());
        setSelectedIndex(-1);

        JButton btnPlus = new JButton(PLUS_ICON);
        btnPlus.setToolTipText("Add a new request");
        btnPlus.setBorderPainted(false);
        btnPlus.setContentAreaFilled(false);
        btnPlus.setFocusable(false);
        btnPlus.setMargin(new Insets(0,0,0,0));
        btnPlus.addActionListener(l ->
                invokeLater(() -> addNewTab(null, new RequestDto()))
        );

        setTabComponentAt(indexOfTab("+"), btnPlus);
    }

    protected void addNewTab(
            @Nullable RequestHeadDto requestHead,
            @NotNull RequestDto request
    ){
        if(!isEventDispatchThread()) throw new IllegalThreadStateException();

        int tabPosition = getTabCount() - 1;
        var key = new TabKey(request.getId());
        var tabId = key.getUuid().toString();
        var requestStagingMonitor = new RequestStagingMonitor(
                key,
                requestHead,
                request
        );

        // setup tab title
        var tabTitle = new TabTitle(
                this,
                key,
                requestStagingMonitor,
                requestHead,
                request,
                l -> removeTab(key)
        );

        insertTab(tabId, null, new LoadingPane(), null, tabPosition);
        setTabComponentAt(tabPosition, tabTitle);
        setSelectedIndex(tabPosition);

        // lazy load content
        invokeLater(() -> {
            var requestSplitPane = new RequestSplitPane(
                    key,
                    requestStagingMonitor,
                    requestHead,
                    request
            );

            setComponentAt(tabPosition, requestSplitPane);

            tabs.add(new Tab(
                    key,
                    tabTitle,
                    requestSplitPane
            ));
        });
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

    protected void removeTab(Tab tab, boolean forceRemove){
        if(!forceRemove && !isSafeToRemoveTab(tab)) return;

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
                .ifPresent(tab -> removeTab(tab, false));
    }

    protected record Tab(
            TabKey tabKey,
            TabTitle tabTitle,
            RequestSplitPane tabContent
    ) { }
}
