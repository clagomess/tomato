package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.factory.IconFactory;
import com.github.clagomess.tomato.ui.MainUi;
import com.github.clagomess.tomato.ui.request.tabrequest.TabRequestUi;
import com.github.clagomess.tomato.ui.request.tabresponse.TabResponseUi;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RequestUi extends JTabbedPane {
    private final MainUi parent;
    private final String ADD_TAB_TITLE = "+";
    private final List<String> tabTitles = new ArrayList<>();

    public RequestUi(MainUi parent){
        this.parent = parent;

        Stream.of("Aoba", "Aoba", "Bene")
                .map(RequestDto::new)
                .forEach(this::addNewTab);

        addChangeListener(e -> {
            if(getSelectedIndex() != indexOfTab(ADD_TAB_TITLE) || getSelectedIndex() == -1) return;
            addNewTab(new RequestDto("New Request"));
        });
    }

    public void addNewTab(RequestDto dto){
        // remove "+" tab
        if(indexOfTab(ADD_TAB_TITLE) != -1) removeTabAt(indexOfTab(ADD_TAB_TITLE));

        // add new
        String uniqueTitle = getUniqueTabTitle(dto.getName());
        addTab(uniqueTitle, getTabContent(dto));
        addTabOptions(uniqueTitle, dto.getMethod());
        tabTitles.add(uniqueTitle);
        addTab(ADD_TAB_TITLE, new JPanel());
        setSelectedIndex(getTabCount() -2);
    }

    public JSplitPane getTabContent(RequestDto dto){
        TabResponseUi tabResponseUi = new TabResponseUi();
        TabRequestUi tabRequestUi = new TabRequestUi(this, dto, tabResponseUi);
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabRequestUi, tabResponseUi);
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
