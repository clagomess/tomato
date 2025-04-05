package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxLinkExternalIcon;
import com.github.clagomess.tomato.ui.request.RequestFrame;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.event.MouseEvent.BUTTON2;
import static java.awt.event.MouseEvent.BUTTON3;

@RequiredArgsConstructor
public class TabTitleMouseListener extends MouseAdapter {
    private static final Icon LINK_EXTERNAL_ICON = new BxLinkExternalIcon();

    private final TabKey tabKey;
    private final RequestTabbedPane parent;

    public void mouseClicked(MouseEvent e) {
        redispatch(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        redispatch(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == BUTTON2) {
            parent.removeTab(tabKey);
            return;
        }

        if(e.getButton() == BUTTON3){
            showPopupMenu(e);
            return;
        }

        redispatch(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        redispatch(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        redispatch(e);
    }

    private void redispatch(MouseEvent e) {
        MouseEvent event = SwingUtilities.convertMouseEvent(e.getComponent(), e, parent);
        parent.dispatchEvent(event);
    }

    private void showPopupMenu(MouseEvent e) {
        JPopupMenu popup = new JPopupMenu();

        popup.add(new JMenuItem("Detach", LINK_EXTERNAL_ICON){{
            addActionListener(e -> detachTab());
        }});

        popup.add(new JMenuItem("Close"){{
            addActionListener(e -> parent.removeTab(tabKey));
        }});

        popup.add(new JMenuItem("Close Other Tabs"){{
            addActionListener(e -> removeOtherTabs());
        }});

        popup.add(new JMenuItem("Close Tabs to the left"){{
            addActionListener(e -> removeLeftTabs());
        }});

        popup.add(new JMenuItem("Close Tabs to the right"){{
            addActionListener(e -> removeRightTabs());
        }});

        popup.show(e.getComponent(), e.getX(), e.getY());
    }

    private void detachTab(){
        var result = parent.getTabs().stream()
                .filter(tab -> tab.tabKey().equals(tabKey))
                .findFirst();

        if(result.isEmpty()) return;

        var tab = result.get();

        new WaitExecution(() -> {
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
