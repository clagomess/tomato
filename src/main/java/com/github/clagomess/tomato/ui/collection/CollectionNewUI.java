package com.github.clagomess.tomato.ui.collection;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.publisher.CollectionPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;

public class CollectionNewUI extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();
    private final CollectionComboBox cbCollectionParent;

    private final CollectionRepository collectionRepository = new CollectionRepository();
    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();

    public CollectionNewUI(
            Component parent,
            CollectionTreeDto selectedCollectionTreeParent
    ){
        setTitle("New Collection");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        cbCollectionParent = new CollectionComboBox(selectedCollectionTreeParent);

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("Name"), "wrap");
        add(txtName, "width 300!, wrap");
        add(new JLabel("Parent"), "wrap");
        add(cbCollectionParent, "width 300!, wrap");
        add(btnSave, "align right");

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnSave.addActionListener(l -> btnSaveAction());
    }

    private void btnSaveAction(){
        new WaitExecution(this, btnSave, () -> {
            CollectionTreeDto parent = cbCollectionParent.getSelectedItem();
            if(parent == null) throw new Exception("Parent is null");

            CollectionDto dto = new CollectionDto(txtName.getText());
            collectionRepository.save(
                    parent.getPath(),
                    dto
            );

            var key = new CollectionPublisher.ParentCollectionId(parent.getId());
            collectionPublisher.getOnChange().publish(key, new PublisherEvent<>(INSERTED, dto.getId()));

            setVisible(false);
            dispose();
        }).execute();
    }
}
