package io.github.clagomess.tomato.ui.main.collection.popupmenu;

import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.ui.collection.CollectionNewFrame;

import javax.swing.*;
import java.awt.*;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.NEW;
import static io.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;

public class DefaultPopupMenu extends JPopupMenu {
    public DefaultPopupMenu(Component parent) {
        var mNewRequest = new JMenuItem("New Request");
        mNewRequest.addActionListener(e -> RequestPublisher.getInstance()
                .getOnLoad()
                .publish(new PublisherEvent<>(NEW, null)));
        add(mNewRequest);

        var mNewCollection = new JMenuItem("New Collection");
        mNewCollection.addActionListener(e -> toFrontIfExists(
                CollectionNewFrame.class,
                () -> new CollectionNewFrame(parent,null)
        ));
        add(mNewCollection);
    }
}
