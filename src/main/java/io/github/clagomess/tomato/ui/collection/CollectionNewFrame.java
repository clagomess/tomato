package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.dto.data.CollectionDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.repository.CollectionRepository;
import io.github.clagomess.tomato.publisher.CollectionPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.ParentCollectionKey;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import io.github.clagomess.tomato.ui.component.undoabletextcomponent.UndoableTextField;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;

public class CollectionNewFrame extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final UndoableTextField txtName = new UndoableTextField();
    private final CollectionComboBox cbCollectionParent;

    private final CollectionRepository collectionRepository = new CollectionRepository();
    private final CollectionPublisher collectionPublisher = CollectionPublisher.getInstance();

    public CollectionNewFrame(
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

            var key = new ParentCollectionKey(parent.getId());
            collectionPublisher.getOnChange().publish(key, new PublisherEvent<>(INSERTED, dto.getId()));

            setVisible(false);
            dispose();
        }).execute();
    }
}
