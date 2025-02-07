package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainUI;
import com.github.clagomess.tomato.ui.collection.CollectionImportUI;
import com.github.clagomess.tomato.ui.collection.CollectionNewUI;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxExportIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxImportIcon;

import javax.swing.*;

public class CollectionMenu extends JMenu {
    public CollectionMenu(MainUI mainUI) {
        super("Collection");

        var mNew = new JMenuItem("New Collection");
        mNew.addActionListener(l -> new CollectionNewUI(mainUI, null));
        add(mNew);

        var mImport = new JMenuItem("Import", new BxImportIcon());
        mImport.addActionListener(l -> new CollectionImportUI(mainUI, null));
        add(mImport);

        var mExport = new JMenuItem("Export", new BxExportIcon());
        mExport.addActionListener(l -> {}); //@TODO: Implements - Export Collection
        add(mExport);
    }
}
