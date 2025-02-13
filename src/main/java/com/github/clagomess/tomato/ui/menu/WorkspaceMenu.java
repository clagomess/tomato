package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainUI;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTransferAltIcon;
import com.github.clagomess.tomato.ui.workspace.WorkspaceNewUI;
import com.github.clagomess.tomato.ui.workspace.WorkspaceSwitchUI;
import com.github.clagomess.tomato.ui.workspace.list.WorkspaceListFrame;

import javax.swing.*;

public class WorkspaceMenu extends JMenu {
    public WorkspaceMenu(MainUI mainUI) {
        super("Workspace");

        var mSwitch = new JMenuItem("Switch", new BxTransferAltIcon());
        mSwitch.addActionListener(e -> new WorkspaceSwitchUI(mainUI));
        add(mSwitch);

        var mNew = new JMenuItem("New Workspace");
        mNew.addActionListener(l -> new WorkspaceNewUI(mainUI));
        add(mNew);

        var mEdit = new JMenuItem("Edit Workspace");
        mEdit.addActionListener(l -> new WorkspaceListFrame(mainUI));
        add(mEdit);
    }
}
