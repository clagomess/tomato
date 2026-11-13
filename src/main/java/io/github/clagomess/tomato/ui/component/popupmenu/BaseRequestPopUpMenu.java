package io.github.clagomess.tomato.ui.component.popupmenu;

import io.github.clagomess.tomato.controller.component.popupmenu.BaseRequestPopUpMenuController;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.*;
import io.github.clagomess.tomato.ui.main.request.codesnippet.CodeSnippetFrame;
import io.github.clagomess.tomato.ui.main.request.left.ViewRenderedUrlFrame;
import io.github.clagomess.tomato.ui.request.RequestDeleteFrame;
import io.github.clagomess.tomato.ui.request.RequestMoveFrame;
import io.github.clagomess.tomato.ui.request.RequestPropertiesFrame;
import io.github.clagomess.tomato.ui.request.RequestRenameFrame;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.clagomess.tomato.ui.component.PreventDefaultFrame.disposeIfExists;

public abstract class BaseRequestPopUpMenu extends JPopupMenu {
    protected static final Icon LINK_EXTERNAL_ICON = new BxLinkExternalIcon();
    private static final Icon GLOBE_ICON = new BxGlobeIcon();
    private static final Icon CODE_ALT_ICON = new BxCodeAltIcon();
    private static final Icon SORT_ALT_2_ICON = new BxSortAlt2Icon();
    private static final Icon TRASH_ICON = new BxTrashIcon();
    private static final Icon INFO_CIRCLE_ICON = new BxInfoCircleIcon();

    private final BaseRequestPopUpMenuController controller = new BaseRequestPopUpMenuController();

    protected void setup(
            Component parent,
            RequestHeadDto requestHead
    ){
        var mViewRenderedUrl = new JMenuItem("View Rendered Url…", GLOBE_ICON);
        mViewRenderedUrl.addActionListener(e -> viewRenderedUrl(parent, requestHead));
        add(mViewRenderedUrl);

        var mCodeSnippet = new JMenuItem("Code Snippet…", CODE_ALT_ICON);
        mCodeSnippet.addActionListener(e -> codeSnippet(parent, requestHead));
        add(mCodeSnippet);

        addSeparator();

        var mRename = new JMenuItem("Rename…");
        mRename.addActionListener(e -> rename(parent, requestHead));
        add(mRename);

        var mDuplicate = new JMenuItem("Duplicate");
        mDuplicate.addActionListener(e -> duplicate(requestHead));
        add(mDuplicate);

        var mMove = new JMenuItem("Move…", SORT_ALT_2_ICON);
        mMove.addActionListener(e -> move(parent, requestHead));
        add(mMove);

        addSeparator();

        var mDelete = new JMenuItem("Delete", TRASH_ICON);
        mDelete.addActionListener(e -> delete(parent, requestHead));
        add(mDelete);

        addSeparator();

        var mProperties = new JMenuItem("Properties", INFO_CIRCLE_ICON);
        mProperties.addActionListener(e -> properties(parent, requestHead));
        add(mProperties);
    }

    protected void viewRenderedUrl(
            Component parent,
            RequestHeadDto requestHead
    ) {
        var request = new AtomicReference<RequestDto>();

        new WaitExecution(parent)
                .thenBackground(() -> request.set(controller.load(requestHead)))
                .thenEdt(() -> disposeIfExists(
                        ViewRenderedUrlFrame.class,
                        () -> new ViewRenderedUrlFrame(parent, request.get())
                ))
                .execute();
    }

    protected void codeSnippet(
            Component parent,
            RequestHeadDto requestHead
    ) {
        var request = new AtomicReference<RequestDto>();

        new WaitExecution(parent)
                .thenBackground(() -> request.set(controller.load(requestHead)))
                .thenEdt(() -> disposeIfExists(
                        CodeSnippetFrame.class,
                        () -> new CodeSnippetFrame(parent, requestHead, request.get())
                ))
                .execute();
    }

    protected void duplicate(RequestHeadDto requestHead) {
        new WaitExecution()
                .thenBackground(() -> controller.duplicate(requestHead))
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

    protected void properties(
            Component parent,
            RequestHeadDto requestHead
    ){
        disposeIfExists(
                RequestPropertiesFrame.class,
                () -> new RequestPropertiesFrame(parent, requestHead)
        );
    }
}
