package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.controller.collection.CollectionExportFrameController;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.ui.BaseFrame;
import io.github.clagomess.tomato.ui.component.ConverterComboBox;
import io.github.clagomess.tomato.ui.component.FileExport;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class CollectionExportFrame
        extends BaseFrame
        implements CollectionExportFrameInterface {
    private final JButton btnExport = new JButton("Export");
    private final ConverterComboBox cbConverter = new ConverterComboBox();
    private final CollectionComboBox cbCollection;
    private final CollectionExportFrameController controller;

    public CollectionExportFrame(
            Component parent,
            CollectionTreeDto selected
    ){
        controller = new CollectionExportFrameController(this);

        setTitle("Export Collection");
        setMinimumSize(new Dimension(300, 100));
        setResizable(false);

        cbCollection = new CollectionComboBox(selected);

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("Collection"), "wrap");
        add(cbCollection, "width 300!, wrap");
        add(new JLabel("Type"), "wrap");
        add(cbConverter, "width 300!, wrap");
        add(btnExport, "align right");

        getRootPane().setDefaultButton(btnExport);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnExport.addActionListener(l -> btnExportAction());
    }

    private void btnExportAction(){
        new WaitExecution(this, btnExport, () -> {
            controller.export(
                    cbCollection.getSelectedItem(),
                    cbConverter.getSelectedItem()
            );

            setVisible(false);
            dispose();
        }).execute();
    }

    public void exportFile(
            String name,
            FileExport.Consumer consumer
    ) throws IOException {
        var file = new FileExport(this);
        file.setSelectedFile(new File(name));
        file.save(consumer);
    }
}
