package com.github.clagomess.tomato.ui.main.collection.popupmenu;

import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.collection.CollectionNewUI;

import javax.swing.*;
import java.awt.*;

public class DefaultPopupMenu extends JPopupMenu {
    public DefaultPopupMenu(Component parent) {
        var mNewRequest = new JMenuItem("New Request");
        mNewRequest.addActionListener(e -> RequestPublisher.getInstance()
                .getOnOpenNew()
                .publish(true));
        add(mNewRequest);

        var mNewCollection = new JMenuItem("New Collection");
        mNewCollection.addActionListener(e -> new CollectionNewUI(
                parent,
                null
        ));
        add(mNewCollection);
    }
}
