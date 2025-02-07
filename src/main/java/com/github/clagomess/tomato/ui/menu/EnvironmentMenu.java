package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainUI;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxExportIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxImportIcon;
import com.github.clagomess.tomato.ui.environment.EnvironmentImportUI;
import com.github.clagomess.tomato.ui.environment.EnvironmentNewUI;
import com.github.clagomess.tomato.ui.environment.list.EnvironmentListUI;

import javax.swing.*;

public class EnvironmentMenu extends JMenu {
    public EnvironmentMenu(MainUI mainUI) {
        super("Environment");

        var mNew = new JMenuItem("New Environment");
        mNew.addActionListener(l -> new EnvironmentNewUI(mainUI));
        add(mNew);

        var mEdit = new JMenuItem("Edit Environments");
        mEdit.addActionListener(l -> new EnvironmentListUI(mainUI));
        add(mEdit);

        var mImport = new JMenuItem("Import", new BxImportIcon());
        mImport.addActionListener(l -> new EnvironmentImportUI(mainUI));
        add(mImport);

        var mExport = new JMenuItem("Export", new BxExportIcon());
        mExport.addActionListener(l -> {}); //@TODO: Implements - Export Environment
        add(mExport);
    }
}
