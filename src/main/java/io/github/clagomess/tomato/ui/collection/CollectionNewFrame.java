package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.controller.collection.CollectionNewFrameController;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import io.github.clagomess.tomato.ui.component.undoabletextcomponent.UndoableTextField;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class CollectionNewFrame extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final UndoableTextField txtName = new UndoableTextField();
    private final CollectionComboBox cbCollectionParent;

    private final CollectionNewFrameController controller;

    public CollectionNewFrame(
            Component parent,
            CollectionTreeDto selectedCollectionTreeParent
    ){
        controller = new CollectionNewFrameController();

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
            controller.save(
                    cbCollectionParent.getSelectedItem(),
                    txtName.getText()
            );

            setVisible(false);
            dispose();
        }).execute();
    }
}
