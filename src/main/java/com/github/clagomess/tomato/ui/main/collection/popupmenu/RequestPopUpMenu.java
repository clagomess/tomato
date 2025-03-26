package com.github.clagomess.tomato.ui.main.collection.popupmenu;

import com.github.clagomess.tomato.controller.main.collection.popupmenu.RequestPopUpMenuController;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxCodeAltIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxLinkExternalIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSortAlt2Icon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import com.github.clagomess.tomato.ui.main.request.codesnippet.CodeSnippetFrame;
import com.github.clagomess.tomato.ui.request.RequestDeleteFrame;
import com.github.clagomess.tomato.ui.request.RequestFrame;
import com.github.clagomess.tomato.ui.request.RequestMoveFrame;
import com.github.clagomess.tomato.ui.request.RequestRenameFrame;

import javax.swing.*;
import java.awt.*;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.LOAD;
import static com.github.clagomess.tomato.ui.component.PreventDefaultFrame.disposeIfExists;

public class RequestPopUpMenu extends JPopupMenu {
    private final RequestPopUpMenuController controller = new RequestPopUpMenuController();

    public RequestPopUpMenu(
            Component parent,
            RequestHeadDto requestHead
    ) {

        var mOpen = new JMenuItem("Open");
        mOpen.addActionListener(e -> open(requestHead));
        add(mOpen);

        var mOpenDetached = new JMenuItem("Open Detached", new BxLinkExternalIcon());
        mOpenDetached.addActionListener(e -> openDetached(requestHead));
        add(mOpenDetached);

        var mCodeSnippet = new JMenuItem("Code Snippet", new BxCodeAltIcon());
        mCodeSnippet.addActionListener(e -> codeSnippet(parent, requestHead));
        add(mCodeSnippet);

        addSeparator();

        var mDuplicate = new JMenuItem("Duplicate");
        mDuplicate.addActionListener(e -> duplicate(requestHead));
        add(mDuplicate);

        var mMove = new JMenuItem("Move", new BxSortAlt2Icon());
        mMove.addActionListener(e -> move(parent, requestHead));
        add(mMove);

        var mRename = new JMenuItem("Rename");
        mRename.addActionListener(e -> rename(parent, requestHead));
        add(mRename);

        addSeparator();

        var mDelete = new JMenuItem("Delete", new BxTrashIcon());
        mDelete.addActionListener(e -> delete(parent, requestHead));
        add(mDelete);
    }

    protected void open(
            RequestHeadDto requestHead
    ) {
        RequestPublisher.getInstance()
                .getOnLoad()
                .publish(new PublisherEvent<>(LOAD, requestHead));
    }

    protected void openDetached(RequestHeadDto requestHead) {
        new WaitExecution(() ->
                new RequestFrame(requestHead, null)
        ).execute();
    }

    protected void codeSnippet(
            Component parent,
            RequestHeadDto requestHead
    ) {
        disposeIfExists(
                CodeSnippetFrame.class,
                () -> new WaitExecution(
                        parent,
                        () -> {
                            var request = controller.load(requestHead);
                            new CodeSnippetFrame(this, request);
                        }
                ).execute()
        );
    }

    protected void duplicate(RequestHeadDto requestHead) {
        new WaitExecution(() -> controller.duplicate(requestHead))
                .execute();
    }

    protected void move(
            Component parent,
            RequestHeadDto requestHead
    ){
        disposeIfExists(
                RequestMoveFrame.class,
                () -> new RequestMoveFrame(parent, requestHead)
        );
    }

    protected void rename(
            Component parent,
            RequestHeadDto requestHead
    ){
        disposeIfExists(
                RequestRenameFrame.class,
                () -> new RequestRenameFrame(parent, requestHead)
        );
    }

    protected void delete(
            Component parent,
            RequestHeadDto requestHead
    ){
        new RequestDeleteFrame(parent, requestHead)
                .showConfirmDialog();
    }
}
