package com.github.clagomess.tomato.ui.main.collection.popupmenu;

import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.ui.collection.CollectionNewFrame;

import javax.swing.*;
import java.awt.*;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.NEW;

public class DefaultPopupMenu extends JPopupMenu {
    public DefaultPopupMenu(Component parent) {
        var mNewRequest = new JMenuItem("New Request");
        mNewRequest.addActionListener(e -> RequestPublisher.getInstance()
                .getOnLoad()
                .publish(new PublisherEvent(NEW, null)));
        add(mNewRequest);

        var mNewCollection = new JMenuItem("New Collection");
        mNewCollection.addActionListener(e -> new CollectionNewFrame(
                parent,
                null
        ));
        add(mNewCollection);
    }
}
