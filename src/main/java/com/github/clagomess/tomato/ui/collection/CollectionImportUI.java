package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.service.DumpPumpService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.FileChooser;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImageIcon;
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

    private final DumpPumpService dumpPumpService = DumpPumpService.getInstance();
    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();

    public CollectionImportUI(
            Component parent,
            CollectionTreeDto selectedCollectionTreeParent
    ){
        setTitle("Import Collection");
        setIconImage(new FaviconImageIcon().getImage());
        setMinimumSize(new Dimension(300, 100));
        setResizable(false);

        cbCollectionParent = new CollectionComboBox(selectedCollectionTreeParent);

        JPanel panel = new JPanel(new MigLayout());
        panel.add(new JLabel("File"), "wrap");
        panel.add(fileChooser, "width 100%, wrap");
        panel.add(new JLabel("Type"), "wrap");
        panel.add(cbType, "width 100%, wrap");
        panel.add(new JLabel("Destination"), "wrap");
        panel.add(cbCollectionParent, "width 100%, wrap");
        panel.add(btnImport, "align right");
        add(panel);

        getRootPane().setDefaultButton(btnImport);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnImport.addActionListener(l -> btnImportAction());
    }

    private void btnImportAction(){
        btnImport.setEnabled(false);

        try {
            CollectionTreeDto parent = cbCollectionParent.getSelectedItem();
            if(parent == null) throw new Exception("Parent is null");

            dumpPumpService.pumpPostmanCollection(
                    parent.getPath(),
                    fileChooser.getValue()
            );

            var key = new CollectionPublisher.ParentCollectionId(parent.getParent().getId());
            collectionPublisher.getOnSave().publish(key, parent);

            setVisible(false);
            dispose();
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        } finally {
            btnImport.setEnabled(true);
        }
    }
}
