package com.github.clagomess.tomato.ui.menu;

import com.github.clagomess.tomato.ui.MainFrame;
import com.github.clagomess.tomato.ui.collection.CollectionExportFrame;
import com.github.clagomess.tomato.ui.collection.CollectionImportFrame;
import com.github.clagomess.tomato.ui.collection.CollectionNewFrame;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxExportIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxImportIcon;

import javax.swing.*;

public class CollectionMenu extends JMenu {
    public CollectionMenu(MainFrame mainFrame) {
        super("Collection");

        var mNew = new JMenuItem("New Collection");
        mNew.addActionListener(l -> new CollectionNewFrame(mainFrame, null));
        add(mNew);

        var mImport = new JMenuItem("Import", new BxImportIcon());
        mImport.addActionListener(l -> new CollectionImportFrame(mainFrame, null));
        add(mImport);

        var mExport = new JMenuItem("Export", new BxExportIcon());
        mExport.addActionListener(l -> new CollectionExportFrame(mainFrame, null));
        add(mExport);
    }
}
