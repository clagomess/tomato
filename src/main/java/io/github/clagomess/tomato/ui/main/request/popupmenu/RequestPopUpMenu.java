package io.github.clagomess.tomato.ui.main.request.popupmenu;

import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.popupmenu.BaseRequestPopUpMenu;
import io.github.clagomess.tomato.ui.main.request.RequestTabbedPane;
import io.github.clagomess.tomato.ui.request.RequestFrame;
import org.jspecify.annotations.Nullable;

import javax.swing.*;

public class RequestPopUpMenu extends BaseRequestPopUpMenu {
    private final RequestTabbedPane parent;
    private final TabKey tabKey;

    public RequestPopUpMenu(
            RequestTabbedPane parent,
            TabKey tabKey,
            @Nullable RequestHeadDto requestHead
    ) {
        this.parent = parent;
        this.tabKey = tabKey;

        var mDetach = new JMenuItem("Detach", LINK_EXTERNAL_ICON);
        mDetach.addActionListener(e -> detachTab());
        add(mDetach);

        var mClose = new JMenuItem("Close");
        mClose.addActionListener(e -> parent.removeTab(tabKey));
        add(mClose);

        var mCloseOthers = new JMenuItem("Close Other Tabs");
        mCloseOthers.addActionListener(e -> removeOtherTabs());
        add(mCloseOthers);

        var mCloseLeft = new JMenuItem("Close Tabs to the left");
        mCloseLeft.addActionListener(e -> removeLeftTabs());
        add(mCloseLeft);

        var mCloseRight = new JMenuItem("Close Tabs to the right");
        mCloseRight.addActionListener(e -> removeRightTabs());
        add(mCloseRight);

        if(requestHead == null) return;

        addSeparator();
        setup(parent, requestHead);
    }

    private void detachTab(){
        var result = parent.getTabs().stream()
                .filter(tab -> tab.tabKey().equals(tabKey))
                .findFirst();

        if(result.isEmpty()) return;

        var tab = result.get();

        new WaitExecution().thenEdt(() -> {
            new RequestFrame(
                    tab.tabContent().getRequestHeadDto(),
                    tab.tabContent().getRequestDto()
            );
            parent.removeTab(tab, true);
        }).execute();
    }

    private void removeLeftTabs(){
        var result = parent.getTabs().stream()
                .takeWhile(tab -> !tab.tabKey().equals(tabKey))
                .toList();

        result.forEach(tab -> parent.removeTab(tab, false));
    }

    private void removeOtherTabs(){
        var result = parent.getTabs().stream()
                .filter(tab -> !tab.tabKey().equals(tabKey))
                .toList();

        result.forEach(tab -> parent.removeTab(tab, false));
    }

    private void removeRightTabs(){
        var result = parent.getTabs().stream()
                .dropWhile(tab -> !tab.tabKey().equals(tabKey))
                .filter(tab -> !tab.tabKey().equals(tabKey))
                .toList();

        result.forEach(tab -> parent.removeTab(tab, false));
    }
}
