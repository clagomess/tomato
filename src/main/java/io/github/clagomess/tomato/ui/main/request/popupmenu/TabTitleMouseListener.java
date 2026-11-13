package io.github.clagomess.tomato.ui.main.request.popupmenu;

import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.ui.main.request.RequestTabbedPane;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;

import static java.awt.event.MouseEvent.BUTTON2;
import static java.awt.event.MouseEvent.BUTTON3;

@RequiredArgsConstructor
public class TabTitleMouseListener extends MouseAdapter {
    private final TabKey tabKey;
    private final RequestTabbedPane parent;
    private final AtomicReference<RequestHeadDto> requestHeadAR;

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
            new RequestPopUpMenu(parent, tabKey, requestHeadAR.get())
                    .show(e.getComponent(), e.getX(), e.getY());
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
}
