package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.controller.collection.CollectionImportFrameController;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.ui.BaseFrame;
import io.github.clagomess.tomato.ui.component.ConverterComboBox;
import io.github.clagomess.tomato.ui.component.FileChooser;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class CollectionImportFrame extends BaseFrame {
    private final JButton btnImport = new JButton("Import");
    private final FileChooser fileChooser = new FileChooser();
    private final ConverterComboBox cbConverter = new ConverterComboBox();
    private final CollectionComboBox cbCollectionParent;
    private final CollectionImportFrameController controller;

    public CollectionImportFrame(
            Component parent,
            CollectionTreeDto selectedCollectionTreeParent
    ){
        controller = new CollectionImportFrameController();

        setTitle("Import Collection");
        setMinimumSize(new Dimension(300, 100));
        setResizable(false);

        cbCollectionParent = new CollectionComboBox(selectedCollectionTreeParent);

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("File"), "wrap");
        add(fileChooser, "width 300!, wrap");
        add(new JLabel("Type"), "wrap");
        add(cbConverter, "width 300!, wrap");
        add(new JLabel("Destination"), "wrap");
        add(cbCollectionParent, "width 300!, wrap");
        add(btnImport, "align right");

        getRootPane().setDefaultButton(btnImport);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnImport.addActionListener(l -> btnImportAction());
    }

    private void btnImportAction(){
        var parent = cbCollectionParent.getSelectedItem();
        var converter = cbConverter.getSelectedItem();
        var file = fileChooser.getValue();

        new WaitExecution(this, btnImport).thenBackground(() ->
            controller.importCollection(
                    parent,
                    converter,
                    file
            )
        ).thenEdt(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Collection imported"
            );

            setVisible(false);
            dispose();
        }).execute();
    }
}
