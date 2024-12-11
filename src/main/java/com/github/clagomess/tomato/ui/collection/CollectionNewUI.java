package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
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
    private final CollectionComboBox cbCollectionParent;

    private final CollectionDataService collectionDataService = CollectionDataService.getInstance();
    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();

    public CollectionNewUI(
            Component parent,
            CollectionTreeDto selectedCollectionTreeParent
    ){
        setTitle("New Collection");
        setIconImage(IconFactory.ICON_FAVICON.getImage());
        setMinimumSize(new Dimension(300, 100));
        setResizable(false);

        cbCollectionParent = new CollectionComboBox(selectedCollectionTreeParent);

        JPanel panel = new JPanel(new MigLayout());
        panel.add(new JLabel("Name"), "wrap");
        panel.add(txtName, "width 100%, wrap");
        panel.add(new JLabel("Parent"), "wrap");
        panel.add(cbCollectionParent, "width 100%, wrap");
        panel.add(btnSave, "align right");
        add(panel);

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
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

            var key = new CollectionPublisher.ParentCollectionId(parent.getId());
            collectionPublisher.getOnSave().publish(key, collectionTree);

            setVisible(false);
            dispose();
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        } finally {
            btnSave.setEnabled(true);
        }
    }
}
