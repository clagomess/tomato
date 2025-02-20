package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainFrame;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxExportIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxImportIcon;
import com.github.clagomess.tomato.ui.environment.EnvironmentExportFrame;
import com.github.clagomess.tomato.ui.environment.EnvironmentImportFrame;
import com.github.clagomess.tomato.ui.environment.EnvironmentNewFrame;
import com.github.clagomess.tomato.ui.environment.list.EnvironmentListFrame;

import javax.swing.*;

public class EnvironmentMenu extends JMenu {
    public EnvironmentMenu(MainFrame mainFrame) {
        super("Environment");

        var mNew = new JMenuItem("New Environment");
        mNew.addActionListener(l -> new EnvironmentNewFrame(mainFrame));
        add(mNew);

        var mEdit = new JMenuItem("Edit Environments");
        mEdit.addActionListener(l -> new EnvironmentListFrame(mainFrame));
        add(mEdit);

        var mImport = new JMenuItem("Import", new BxImportIcon());
        mImport.addActionListener(l -> new EnvironmentImportFrame(mainFrame));
        add(mImport);

        var mExport = new JMenuItem("Export", new BxExportIcon());
        mExport.addActionListener(l -> new EnvironmentExportFrame(mainFrame));
        add(mExport);
    }
}
