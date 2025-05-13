package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.controller.collection.CollectionExportFrameController;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.ui.component.ConverterComboBox;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Optional;

public class CollectionExportFrame
        extends JFrame
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
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

            JOptionPane.showMessageDialog(
                    this,
                    "File Saved Successfully"
            );

            setVisible(false);
            dispose();
        }).execute();
    }

    public Optional<File> getExportFile(String name) {
        JFileChooser file = new JFileChooser();
        file.setFileSelectionMode(JFileChooser.FILES_ONLY);
        file.setSelectedFile(new File(name));

        if(file.showSaveDialog(this) != JFileChooser.APPROVE_OPTION){
            return Optional.empty();
        }

        return Optional.of(file.getSelectedFile());
    }
}
