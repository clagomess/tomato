package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainFrame;
import com.github.clagomess.tomato.ui.collection.CollectionExportFrame;
import com.github.clagomess.tomato.ui.collection.CollectionImportFrame;
import com.github.clagomess.tomato.ui.collection.CollectionNewFrame;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxExportIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxImportIcon;

import javax.swing.*;

import static com.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;

public class CollectionMenu extends JMenu {
    public CollectionMenu(MainFrame mainFrame) {
        super("Collection");

        var mNew = new JMenuItem("New Collection");
        mNew.addActionListener(l -> toFrontIfExists(
                CollectionNewFrame.class,
                () -> new CollectionNewFrame(mainFrame, null)
        ));
        add(mNew);

        var mImport = new JMenuItem("Import", new BxImportIcon());
        mImport.addActionListener(l -> toFrontIfExists(
                CollectionImportFrame.class,
                () -> new CollectionImportFrame(mainFrame, null)
        ));
        add(mImport);

        var mExport = new JMenuItem("Export", new BxExportIcon());
        mExport.addActionListener(l -> toFrontIfExists(
                CollectionExportFrame.class,
                () -> new CollectionExportFrame(mainFrame, null)
        ));
        add(mExport);
    }
}
