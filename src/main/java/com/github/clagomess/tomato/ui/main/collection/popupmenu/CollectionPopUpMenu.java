package com.github.clagomess.tomato.ui.main.collection.popupmenu;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.ui.collection.*;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxExportIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxImportIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSortAlt2Icon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;

import javax.swing.*;
import java.awt.*;

import static com.github.clagomess.tomato.ui.component.PreventDefaultFrame.disposeIfExists;

public class CollectionPopUpMenu extends JPopupMenu {
    public CollectionPopUpMenu(
            Component parent,
            CollectionTreeDto collectionTree
    ) {
        var mMove = new JMenuItem("Move", new BxSortAlt2Icon());
        mMove.addActionListener(e -> disposeIfExists(
                CollectionMoveFrame.class,
                () -> new CollectionMoveFrame(parent, collectionTree)
        ));
        add(mMove);

        var mRename = new JMenuItem("Rename");
        mRename.addActionListener(e -> disposeIfExists(
                CollectionRenameFrame.class,
                () -> new CollectionRenameFrame(parent, collectionTree)
        ));
        add(mRename);

        addSeparator();

        var mNewCollection = new JMenuItem("New Collection");
        mNewCollection.addActionListener(ae -> disposeIfExists(
                CollectionNewFrame.class,
                () -> new CollectionNewFrame(parent, collectionTree)
        ));
        add(mNewCollection);

        var mImport = new JMenuItem("Import", new BxImportIcon());
        mImport.addActionListener(ae -> disposeIfExists(
                CollectionImportFrame.class,
                () -> new CollectionImportFrame(parent, collectionTree)
        ));
        add(mImport);

        var mExport = new JMenuItem("Export", new BxExportIcon());
        mExport.addActionListener(ae -> disposeIfExists(
                CollectionExportFrame.class,
                () -> new CollectionExportFrame(parent, collectionTree)
        ));
        add(mExport);

        addSeparator();

        var mDelete = new JMenuItem("Delete", new BxTrashIcon());
        mDelete.addActionListener(ae -> new CollectionDeleteDialog(parent, collectionTree).showConfirmDialog());
        add(mDelete);
    }
}
