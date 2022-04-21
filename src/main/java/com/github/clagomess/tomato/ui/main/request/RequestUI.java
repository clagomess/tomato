package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.factory.IconFactory;
import com.github.clagomess.tomato.ui.main.request.tabrequest.TabRequestUI;
import com.github.clagomess.tomato.ui.main.request.tabresponse.TabResponseUI;
import com.github.clagomess.tomato.util.UIPublisherUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RequestUI extends JTabbedPane {
    private final String ADD_TAB_TITLE = "+";
    private final List<String> tabTitles = new ArrayList<>();

    public RequestUI(){
        addChangeListener(e -> {
            if(getSelectedIndex() != indexOfTab(ADD_TAB_TITLE) || getSelectedIndex() == -1) return;
            addNewTab(new RequestDto());
        });
        addNewTab(new RequestDto()); //@TODO: melhorar

        UIPublisherUtil.getInstance().getSwitchWorkspaceFIList().add(w -> {
            removeAll();
            tabTitles.clear();
            addNewTab(new RequestDto());
        });
    }

    public void addNewTab(RequestDto dto){
        // remove "+" tab
        if(indexOfTab(ADD_TAB_TITLE) != -1) removeTabAt(indexOfTab(ADD_TAB_TITLE));

        // tab content
        TabResponseUI tabResponseUi = new TabResponseUI();
        TabRequestUI tabRequestUi = new TabRequestUI(dto, tabResponseUi);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabRequestUi, tabResponseUi);

        // add new
        String uniqueTitle = getUniqueTabTitle(dto.getName());
        addTab(uniqueTitle, splitPane);
        addTabOptions(uniqueTitle, dto.getMethod());
        tabTitles.add(uniqueTitle);

        // add "+" tab
        addTab(ADD_TAB_TITLE, new JPanel());
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
            int index = indexOfTab(tabTitle);
            setSelectedIndex(0);
            tabTitles.remove(tabTitle);
            removeTabAt(index);
        });
    }
}
