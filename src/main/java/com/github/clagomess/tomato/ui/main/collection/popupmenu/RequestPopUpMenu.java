package com.github.clagomess.tomato.ui.main.collection.popupmenu;

import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.mapper.RequestMapper;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxLinkExternalIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSortAlt2Icon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import com.github.clagomess.tomato.ui.request.RequestDeleteFrame;
import com.github.clagomess.tomato.ui.request.RequestFrame;
import com.github.clagomess.tomato.ui.request.RequestMoveFrame;
import com.github.clagomess.tomato.ui.request.RequestRenameFrame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;
import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.LOAD;

public class RequestPopUpMenu extends JPopupMenu {
    public RequestPopUpMenu(
            Component parent,
            RequestHeadDto requestHead
    ) {

        var mOpen = new JMenuItem("Open");
        mOpen.addActionListener(e -> RequestPublisher.getInstance()
                .getOnLoad()
                .publish(new PublisherEvent<>(LOAD, requestHead)));
        add(mOpen);

        var mOpenDetached = new JMenuItem("Open Detached", new BxLinkExternalIcon());
        mOpenDetached.addActionListener(e -> new WaitExecution(() ->
                new RequestFrame(requestHead, null)
        ).execute());
        add(mOpenDetached);

        addSeparator();

        var mDuplicate = new JMenuItem("Duplicate");
        mDuplicate.addActionListener(e -> new WaitExecution(() -> duplicate(requestHead)).execute());
        add(mDuplicate);

        var mMove = new JMenuItem("Move", new BxSortAlt2Icon());
        mMove.addActionListener(e -> new RequestMoveFrame(parent, requestHead));
        add(mMove);

        var mRename = new JMenuItem("Rename");
        mRename.addActionListener(e -> new RequestRenameFrame(parent, requestHead));
        add(mRename);

        addSeparator();

        var mDelete = new JMenuItem("Delete", new BxTrashIcon());
        mDelete.addActionListener(e -> new RequestDeleteFrame(parent, requestHead).showConfirmDialog());
        add(mDelete);
    }

    protected void duplicate(RequestHeadDto requestHead) throws IOException {
        var requestRepository = new RequestRepository();

        var request = requestRepository.load(requestHead)
                .map(RequestMapper.INSTANCE::duplicate)
                .orElseThrow();
        request.setName(request.getName() + " Copy");

        var filePath = requestRepository.save(
                requestHead.getParent().getPath(),
                request
        );

        var newRequestHead = RequestMapper.INSTANCE.toRequestHead(
                request,
                requestHead.getParent(),
                filePath
        );

        RequestPublisher.getInstance()
                .getOnChange()
                .publish(
                        new RequestKey(newRequestHead),
                        new PublisherEvent<>(INSERTED, newRequestHead)
                );
    }
}
