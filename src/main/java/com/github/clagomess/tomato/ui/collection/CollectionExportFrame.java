package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.converter.PostmanConverter;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CollectionExportFrame extends JFrame {
    private final JButton btnExport = new JButton("Export");
    private final JComboBox<String> cbType = new JComboBox<>(new String[]{
            "Postman Collection v2.1.0"
    });
    private final CollectionComboBox cbCollection;

    private final PostmanConverter postmanConverter = new PostmanConverter();

    public CollectionExportFrame(
            Component parent,
            CollectionTreeDto selected
    ){
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
        add(cbType, "width 300!, wrap");
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
            CollectionTreeDto selected = cbCollection.getSelectedItem();
            if(selected == null) throw new Exception("Selected is null");

            JFileChooser file = new JFileChooser();
            file.setFileSelectionMode(JFileChooser.FILES_ONLY);
            file.setSelectedFile(new File(selected.getName() + ".collection.json"));

            if(file.showSaveDialog(this) != JFileChooser.APPROVE_OPTION){
                return;
            }

            postmanConverter.dumpCollection(
                    file.getSelectedFile(),
                    selected
            );

            setVisible(false);
            dispose();
        }).execute();
    }
}
