package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainFrame;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTransferAltIcon;
import com.github.clagomess.tomato.ui.workspace.WorkspaceNewFrame;
import com.github.clagomess.tomato.ui.workspace.WorkspaceSwitchFrame;
import com.github.clagomess.tomato.ui.workspace.list.WorkspaceListFrame;

import javax.swing.*;

public class WorkspaceMenu extends JMenu {
    public WorkspaceMenu(MainFrame mainFrame) {
        super("Workspace");

        var mSwitch = new JMenuItem("Switch", new BxTransferAltIcon());
        mSwitch.addActionListener(e -> new WorkspaceSwitchFrame(mainFrame));
        add(mSwitch);

        var mNew = new JMenuItem("New Workspace");
        mNew.addActionListener(l -> new WorkspaceNewFrame(mainFrame));
        add(mNew);

        var mEdit = new JMenuItem("Edit Workspace");
        mEdit.addActionListener(l -> new WorkspaceListFrame(mainFrame));
        add(mEdit);
    }
}
