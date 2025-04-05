package io.github.clagomess.tomato.ui.menu;

import io.github.clagomess.tomato.ui.MainFrame;
import io.github.clagomess.tomato.ui.collection.CollectionExportFrame;
import io.github.clagomess.tomato.ui.collection.CollectionImportFrame;
import io.github.clagomess.tomato.ui.collection.CollectionNewFrame;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxExportIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxImportIcon;

import javax.swing.*;

import static io.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;

public class CollectionMenu extends JMenu {
    private static final Icon IMPORT_ICON = new BxImportIcon();
    private static final Icon EXPORT_ICON = new BxExportIcon();

    public CollectionMenu(MainFrame mainFrame) {
        super("Collection");

        var mNew = new JMenuItem("New Collection");
        mNew.addActionListener(l -> toFrontIfExists(
                CollectionNewFrame.class,
                () -> new CollectionNewFrame(mainFrame, null)
        ));
        add(mNew);

        var mImport = new JMenuItem("Import", IMPORT_ICON);
        mImport.addActionListener(l -> toFrontIfExists(
                CollectionImportFrame.class,
                () -> new CollectionImportFrame(mainFrame, null)
        ));
        add(mImport);

        var mExport = new JMenuItem("Export", EXPORT_ICON);
        mExport.addActionListener(l -> toFrontIfExists(
                CollectionExportFrame.class,
                () -> new CollectionExportFrame(mainFrame, null)
        ));
        add(mExport);
    }
}
