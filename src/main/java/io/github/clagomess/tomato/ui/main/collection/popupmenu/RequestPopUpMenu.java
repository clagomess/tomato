package io.github.clagomess.tomato.ui.main.collection.popupmenu;

import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.popupmenu.BaseRequestPopUpMenu;
import io.github.clagomess.tomato.ui.request.RequestFrame;

import javax.swing.*;
import java.awt.*;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.LOAD;

public class RequestPopUpMenu extends BaseRequestPopUpMenu {
    public RequestPopUpMenu(
            Component parent,
            RequestHeadDto requestHead
    ) {

        var mOpen = new JMenuItem("Open");
        mOpen.setFont(mOpen.getFont().deriveFont(Font.BOLD));
        mOpen.addActionListener(e -> open(requestHead));
        add(mOpen);

        var mOpenDetached = new JMenuItem("Open Detached", LINK_EXTERNAL_ICON);
        mOpenDetached.addActionListener(e -> openDetached(requestHead));
        add(mOpenDetached);

        addSeparator();
        setup(parent, requestHead);
    }

    protected void open(
            RequestHeadDto requestHead
    ) {
        RequestPublisher.getInstance()
                .getOnLoad()
                .publish(new PublisherEvent<>(LOAD, requestHead));
    }

    protected void openDetached(RequestHeadDto requestHead) {
        new WaitExecution()
                .thenEdt(() -> new RequestFrame(requestHead, null))
                .execute();
    }
}
