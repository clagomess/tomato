package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.controller.collection.CollectionPropertiesFrameController;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.ui.BaseFrame;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;

public class CollectionPropertiesFrame extends BaseFrame {
    private final CollectionPropertiesFrameController controller;

    public CollectionPropertiesFrame(
            Component parent,
            @NonNull CollectionTreeDto collectionTree
    ) {
        this(parent, collectionTree, new CollectionPropertiesFrameController());
    }

    protected CollectionPropertiesFrame(
            Component parent,
            @NonNull CollectionTreeDto collectionTree,
            CollectionPropertiesFrameController controller
    ) {
        this.controller = controller;

        setTitle("Collection Properties");
        setResizable(false);

        setLayout(new MigLayout(
                "insets 10",
                "[][grow, fill]"
        ));

        var properties = this.controller.properties(collectionTree);

        addProperty("ID", properties.id());
        addProperty("Name", properties.name());
        addProperty("Directory", properties.fileName());
        addProperty("Size", properties.fileSize());
        addProperty("Files", properties.fileCount());
        addProperty("Last Modified", properties.fileLastModified());

        var btnClose = new JButton("Close");
        btnClose.addActionListener(l -> dispose());
        add(btnClose, "span 2, align right");

        getRootPane().setDefaultButton(btnClose);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void addProperty(String label, String value){
        add(new JLabel(label));

        var txtValue = new JTextField(value);
        txtValue.setEditable(false);
        txtValue.setCaretPosition(0);
        add(txtValue, "width 400!, wrap");
    }
}
