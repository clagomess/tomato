package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainFrame;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxExportIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxImportIcon;
import com.github.clagomess.tomato.ui.environment.EnvironmentExportFrame;
import com.github.clagomess.tomato.ui.environment.EnvironmentImportFrame;
import com.github.clagomess.tomato.ui.environment.EnvironmentNewFrame;
import com.github.clagomess.tomato.ui.environment.list.EnvironmentListFrame;

import javax.swing.*;

import static com.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;

public class EnvironmentMenu extends JMenu {
    public EnvironmentMenu(MainFrame mainFrame) {
        super("Environment");

        var mNew = new JMenuItem("New Environment");
        mNew.addActionListener(l -> toFrontIfExists(
                EnvironmentNewFrame.class,
                () -> new EnvironmentNewFrame(mainFrame)
        ));
        add(mNew);

        var mEdit = new JMenuItem("Edit Environments");
        mEdit.addActionListener(l -> toFrontIfExists(
                EnvironmentListFrame.class,
                () -> new EnvironmentListFrame(mainFrame)
        ));
        add(mEdit);

        var mImport = new JMenuItem("Import", new BxImportIcon());
        mImport.addActionListener(l -> toFrontIfExists(
                EnvironmentImportFrame.class,
                () -> new EnvironmentImportFrame(mainFrame)
        ));
        add(mImport);

        var mExport = new JMenuItem("Export", new BxExportIcon());
        mExport.addActionListener(l -> toFrontIfExists(
                EnvironmentExportFrame.class,
                () -> new EnvironmentExportFrame(mainFrame)
        ));
        add(mExport);
    }
}
