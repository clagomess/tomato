package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.CollectionDto;
import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.service.CollectionDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.IconFactory;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class CollectionNewUI extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();
    private final CollectionComboBox cbCollectionParent = new CollectionComboBox();

    private final CollectionDataService collectionDataService = CollectionDataService.getInstance();
    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();

    public CollectionNewUI(){
        setTitle("New Collection");
        setIconImage(IconFactory.ICON_FAVICON.getImage());

        JPanel panel = new JPanel(new MigLayout());
        panel.add(new JLabel("Name"), "wrap");
        panel.add(txtName, "width 100%, wrap");
        panel.add(new JLabel("Parent"), "wrap");
        panel.add(cbCollectionParent, "width 100%, wrap");
        panel.add(btnSave, "align right");

        add(panel);
        setMinimumSize(new Dimension(300, 100));
        setResizable(false);
        pack();
        setVisible(true);

        // set data
        btnSave.addActionListener(l -> btnSaveAction());
    }

    private void btnSaveAction(){
        btnSave.setEnabled(false);

        try {
            CollectionTreeDto parent = cbCollectionParent.getSelectedItem();
            if(parent == null) throw new Exception("Parent is null");

            CollectionDto dto = new CollectionDto(txtName.getText());
            collectionDataService.save(
                    parent.getPath(),
                    dto
            );

            var collectionTree = collectionDataService.getCollectionRootTree(
                    parent,
                    dto.getId()
            );

            collectionPublisher.getOnInsert().publish(
                    parent.getId(),
                    collectionTree
            );

            setVisible(false);
            dispose();
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        } finally {
            btnSave.setEnabled(true);
        }
    }
}
