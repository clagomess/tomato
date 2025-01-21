package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.converter.PostmanConverter;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.ui.component.FileChooser;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class CollectionImportUI extends JFrame {
    private final JButton btnImport = new JButton("Import");
    private final FileChooser fileChooser = new FileChooser();
    private final JComboBox<String> cbType = new JComboBox<>(new String[]{
            "Postman Collection v2.1.0"
    });
    private final CollectionComboBox cbCollectionParent;

    private final PostmanConverter postmanConverter = new PostmanConverter();
    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();

    public CollectionImportUI(
            Component parent,
            CollectionTreeDto selectedCollectionTreeParent
    ){
        setTitle("Import Collection");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        cbCollectionParent = new CollectionComboBox(selectedCollectionTreeParent);

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("File"), "wrap");
        add(fileChooser, "width 300!, wrap");
        add(new JLabel("Type"), "wrap");
        add(cbType, "width 300!, wrap");
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
        new WaitExecution(this, btnImport, () -> {
            CollectionTreeDto parent = cbCollectionParent.getSelectedItem();
            if(parent == null) throw new Exception("Parent is null");

            postmanConverter.pumpCollection(
                    parent.getPath(),
                    fileChooser.getValue()
            );

            var key = new CollectionPublisher.ParentCollectionId(parent.getParent().getId());
            collectionPublisher.getOnSave().publish(key, parent);

            setVisible(false);
            dispose();
        }).execute();
    }
}
