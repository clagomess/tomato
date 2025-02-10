package com.github.clagomess.tomato.ui.main.collection.popupmenu;

import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxLinkExternalIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSortAlt2Icon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import com.github.clagomess.tomato.ui.request.RequestDeleteUI;
import com.github.clagomess.tomato.ui.request.RequestMoveUI;
import com.github.clagomess.tomato.ui.request.RequestRenameUI;
import com.github.clagomess.tomato.ui.request.RequestUI;

import javax.swing.*;
import java.awt.*;

public class RequestPopUpMenu extends JPopupMenu {
    public RequestPopUpMenu(
            Component parent,
            RequestHeadDto requestHead
    ) {

        var mOpen = new JMenuItem("Open");
        mOpen.addActionListener(e -> RequestPublisher.getInstance().getOnLoad().publish(requestHead));
        add(mOpen);

        var mOpenDetached = new JMenuItem("Open Detached", new BxLinkExternalIcon());
        mOpenDetached.addActionListener(e -> new WaitExecution(() -> new RequestUI(requestHead)).execute());
        add(mOpenDetached);

        addSeparator();

        var mMove = new JMenuItem("Move", new BxSortAlt2Icon());
        mMove.addActionListener(e -> new RequestMoveUI(parent, requestHead));
        add(mMove);

        var mRename = new JMenuItem("Rename");
        mRename.addActionListener(e -> new RequestRenameUI(parent, requestHead));
        add(mRename);

        addSeparator();

        var mDelete = new JMenuItem("Delete", new BxTrashIcon());
        mDelete.addActionListener(e -> new RequestDeleteUI(parent, requestHead).showConfirmDialog());
        add(mDelete);
    }
}
