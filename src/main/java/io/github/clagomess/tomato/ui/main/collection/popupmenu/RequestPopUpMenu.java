package io.github.clagomess.tomato.ui.main.collection.popupmenu;

import io.github.clagomess.tomato.controller.main.collection.popupmenu.RequestPopUpMenuController;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxCodeAltIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxLinkExternalIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSortAlt2Icon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import io.github.clagomess.tomato.ui.main.request.codesnippet.CodeSnippetFrame;
import io.github.clagomess.tomato.ui.request.RequestDeleteFrame;
import io.github.clagomess.tomato.ui.request.RequestFrame;
import io.github.clagomess.tomato.ui.request.RequestMoveFrame;
import io.github.clagomess.tomato.ui.request.RequestRenameFrame;

import javax.swing.*;
import java.awt.*;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.LOAD;
import static io.github.clagomess.tomato.ui.component.PreventDefaultFrame.disposeIfExists;

public class RequestPopUpMenu extends JPopupMenu {
    private static final Icon LINK_EXTERNAL_ICON = new BxLinkExternalIcon();
    private static final Icon CODE_ALT_ICON = new BxCodeAltIcon();
    private static final Icon SORT_ALT_2_ICON = new BxSortAlt2Icon();
    private static final Icon TRASH_ICON = new BxTrashIcon();

    private final RequestPopUpMenuController controller = new RequestPopUpMenuController();

    public RequestPopUpMenu(
            Component parent,
            RequestHeadDto requestHead
    ) {

        var mOpen = new JMenuItem("Open");
        mOpen.addActionListener(e -> open(requestHead));
        add(mOpen);

        var mOpenDetached = new JMenuItem("Open Detached", LINK_EXTERNAL_ICON);
        mOpenDetached.addActionListener(e -> openDetached(requestHead));
        add(mOpenDetached);

        var mCodeSnippet = new JMenuItem("Code Snippet", CODE_ALT_ICON);
        mCodeSnippet.addActionListener(e -> codeSnippet(parent, requestHead));
        add(mCodeSnippet);

        addSeparator();

        var mDuplicate = new JMenuItem("Duplicate");
        mDuplicate.addActionListener(e -> duplicate(requestHead));
        add(mDuplicate);

        var mMove = new JMenuItem("Move", SORT_ALT_2_ICON);
        mMove.addActionListener(e -> move(parent, requestHead));
        add(mMove);

        var mRename = new JMenuItem("Rename");
        mRename.addActionListener(e -> rename(parent, requestHead));
        add(mRename);

        addSeparator();

        var mDelete = new JMenuItem("Delete", TRASH_ICON);
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
