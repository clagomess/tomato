package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.factory.DialogFactory;
import com.github.clagomess.tomato.factory.IconFactory;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.service.RequestDataService;
import com.github.clagomess.tomato.ui.main.request.tabrequest.TabRequestUI;
import com.github.clagomess.tomato.ui.main.request.tabresponse.TabResponseUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestUI extends JTabbedPane {
    private final List<String> tabTitles = new ArrayList<>();

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
            tabTitles.forEach(tabTitle -> removeTabAt(indexOfTab(tabTitle)));
            tabTitles.clear();
        });

        requestPublisher.getOnLoad().addListener(event -> {
            try {
                requestDataService.load(event).ifPresent(this::addNewTab);
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
            addNewTab(new RequestDto());
        });
    }

    public void addNewTab(RequestDto dto){
        // tab content
        TabResponseUI tabResponseUi = new TabResponseUI();
        TabRequestUI tabRequestUi = new TabRequestUI(dto, tabResponseUi);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabRequestUi, tabResponseUi);

        // add new
        String uniqueTitle = getUniqueTabTitle(dto.getName());
        insertTab(uniqueTitle, null, splitPane, null, getTabCount() - 1);
        addTabOptions(uniqueTitle, dto.getMethod());
        tabTitles.add(uniqueTitle);

        setSelectedIndex(getTabCount() -2);
    }

    private String getUniqueTabTitle(String tabTitle){
        String newTabTitle = tabTitle;
        int numNewTab = 1;

        while (tabTitles.contains(newTabTitle)){
            newTabTitle = tabTitle + " - " + numNewTab;
            numNewTab++;
        }

        return newTabTitle;
    }

    private void addTabOptions(String tabTitle, HttpMethodEnum httpMethod){
        JPanel pnlTab = new JPanel(new MigLayout("insets 0 0 0 0"));
        pnlTab.setOpaque(false);

        JButton btnClose = new JButton("x");
        btnClose.setBorder(BorderFactory.createEmptyBorder());

        pnlTab.add(new JLabel(IconFactory.createHttpMethodIcon(httpMethod)));
        pnlTab.add(new JLabel(tabTitle));
        pnlTab.add(btnClose);

        setTabComponentAt(indexOfTab(tabTitle), pnlTab);

        btnClose.addActionListener(l -> {
            removeTabAt(indexOfTab(tabTitle));
            tabTitles.remove(tabTitle);
        });
    }
}
