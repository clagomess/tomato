package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.factory.IconFactory;
import com.github.clagomess.tomato.ui.request.tabrequest.TabRequestUi;
import com.github.clagomess.tomato.ui.request.tabresponse.TabResponseUi;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestUi extends JTabbedPane {
    private final String ADD_TAB_TITLE = "+";
    private final List<String> tabTitles = new ArrayList<>();

    public RequestUi(){
        Arrays.asList("Aoba", "Aoba", "Bene").forEach(this::addNewTab);
        addTab(ADD_TAB_TITLE, new JPanel());

        addChangeListener(e -> {
            if(getSelectedIndex() != indexOfTab(ADD_TAB_TITLE) || getSelectedIndex() == -1) return;

            int index = getSelectedIndex();
            removeTabAt(index);
            addNewTab("New Request");
            setSelectedIndex(index);
            addTab(ADD_TAB_TITLE, new JPanel());
        });
    }

    public void addNewTab(String title){
        String uniqueTitle = getUniqueTabTitle(title);
        addTab(uniqueTitle, getTabContent());
        addTabOptions(uniqueTitle);
        tabTitles.add(uniqueTitle);
    }

    public JSplitPane getTabContent(){
        TabRequestUi tabRequestUi = new TabRequestUi();
        TabResponseUi tabResponseUi = new TabResponseUi();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabRequestUi, tabResponseUi);

        return splitPane;
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

    private void addTabOptions(String tabTitle){
        JPanel pnlTab = new JPanel(new MigLayout("insets 0 0 0 0"));
        pnlTab.setOpaque(false);

        JButton btnClose = new JButton("x");
        btnClose.setBorder(BorderFactory.createEmptyBorder());

        pnlTab.add(new JLabel(IconFactory.ICON_HTTP_METHOD_GET));
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
