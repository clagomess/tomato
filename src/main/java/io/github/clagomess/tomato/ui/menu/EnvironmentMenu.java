package io.github.clagomess.tomato.ui.menu;

import io.github.clagomess.tomato.ui.MainFrame;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxExportIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxImportIcon;
import io.github.clagomess.tomato.ui.environment.EnvironmentExportFrame;
import io.github.clagomess.tomato.ui.environment.EnvironmentImportFrame;
import io.github.clagomess.tomato.ui.environment.EnvironmentNewFrame;
import io.github.clagomess.tomato.ui.environment.list.EnvironmentListFrame;

import javax.swing.*;

import static io.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;

public class EnvironmentMenu extends JMenu {
    private static final Icon IMPORT_ICON = new BxImportIcon();
    private static final Icon EXPORT_ICON = new BxExportIcon();

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

        var mImport = new JMenuItem("Import", IMPORT_ICON);
        mImport.addActionListener(l -> toFrontIfExists(
                EnvironmentImportFrame.class,
                () -> new EnvironmentImportFrame(mainFrame)
        ));
        add(mImport);

        var mExport = new JMenuItem("Export", EXPORT_ICON);
        mExport.addActionListener(l -> toFrontIfExists(
                EnvironmentExportFrame.class,
                () -> new EnvironmentExportFrame(mainFrame)
        ));
        add(mExport);
    }
}
