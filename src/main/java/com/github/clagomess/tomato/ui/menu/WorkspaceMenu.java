package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainFrame;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTransferAltIcon;
import com.github.clagomess.tomato.ui.workspace.WorkspaceNewFrame;
import com.github.clagomess.tomato.ui.workspace.WorkspaceSwitchFrame;
import com.github.clagomess.tomato.ui.workspace.list.WorkspaceListFrame;

import javax.swing.*;

import static com.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;

public class WorkspaceMenu extends JMenu {
    private static final Icon TRANSFER_ALT_ICON = new BxTransferAltIcon();

    public WorkspaceMenu(MainFrame mainFrame) {
        super("Workspace");

        var mSwitch = new JMenuItem("Switch", TRANSFER_ALT_ICON);
        mSwitch.addActionListener(e -> toFrontIfExists(
                WorkspaceSwitchFrame.class,
                () -> new WorkspaceSwitchFrame(mainFrame)
        ));
        add(mSwitch);

        var mNew = new JMenuItem("New Workspace");
        mNew.addActionListener(l -> toFrontIfExists(
                WorkspaceNewFrame.class,
                () -> new WorkspaceNewFrame(mainFrame)
        ));
        add(mNew);

        var mEdit = new JMenuItem("Edit Workspace");
        mEdit.addActionListener(l -> toFrontIfExists(
                WorkspaceListFrame.class,
                () -> new WorkspaceListFrame(mainFrame)
        ));
        add(mEdit);
    }
}
