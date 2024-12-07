package com.github.clagomess.tomato.ui;

import com.github.clagomess.tomato.dto.CollectionDto;
import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.factory.DialogFactory;
import com.github.clagomess.tomato.factory.IconFactory;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class RequestSaveUI extends JFrame {
    private final RequestDto requestDto;
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();
    private final JComboBox<CollectionDto> cbCollection = new JComboBox<>();

    public RequestSaveUI(RequestDto dto){
        this.requestDto = dto;

        setTitle("Save Request");
        setIconImage(IconFactory.ICON_FAVICON.getImage());

        JPanel panel = new JPanel(new MigLayout());
        panel.add(new JLabel("Request Name"), "wrap");
        panel.add(txtName, "width 100%, wrap");
        panel.add(new JLabel("Collection"), "wrap");
        panel.add(cbCollection, "width 100%, wrap");
        panel.add(btnSave, "align right");

        add(panel);
        setMinimumSize(new Dimension(300, 100));
        setResizable(false);
        pack();
        setVisible(true);

        // set data
        /* @TODO: check
        DataService.getInstance().getCurrentWorkspace().getCollections()
                .forEach(cbCollection::addItem);

         */
        txtName.setText(dto.getName());
        btnSave.addActionListener(l -> btnSaveAction());
    }

    private void btnSaveAction(){
        btnSave.setEnabled(false);
        this.requestDto.setName(this.txtName.getText());

        try {
            /* @TODO: check
            DataService.getInstance().saveRequest(
                    DataService.getInstance().getCurrentWorkspace().getId(),
                    ((CollectionDto) cbCollection.getSelectedItem()).getId(),
                    this.requestDto
            );
             */

            ((CollectionDto) cbCollection.getSelectedItem()).addOrReplaceRequest(this.requestDto);

            setVisible(false);
            dispose();
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        } finally {
            btnSave.setEnabled(true);
        }
    }
}
