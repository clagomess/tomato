package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.dto.RequestChangeDto;
import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.mapper.RequestMapper;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.service.RequestDataService;
import com.github.clagomess.tomato.ui.collection.CollectionComboBox;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.IconFactory;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class RequestSaveUI extends JFrame {
    private final RequestDto requestDto;
    private final RequestChangeDto requestChangeDto;
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();
    private final CollectionComboBox cbCollection = new CollectionComboBox();

    private final RequestDataService requestDataService = RequestDataService.getInstance();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public RequestSaveUI(
            RequestDto dto,
            RequestChangeDto requestChangeDto
    ){
        this.requestDto = dto;
        this.requestChangeDto = requestChangeDto;

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
        txtName.setText(dto.getName());
        btnSave.addActionListener(l -> btnSaveAction());
    }

    private void btnSaveAction(){
        btnSave.setEnabled(false);
        this.requestDto.setName(this.txtName.getText());

        try {
            CollectionTreeDto parent = cbCollection.getSelectedItem();
            if(parent == null) throw new Exception("Parent is null");

            var filePath = requestDataService.save(
                    parent.getPath(),
                    requestDto
            );

            CollectionTreeDto.Request requestHead = RequestMapper.INSTANCE.toRequestHead(
                    requestDto,
                    parent,
                    filePath
            );

            requestPublisher.getOnSave().publish(requestHead);
            requestChangeDto.reset(requestDto);

            setVisible(false);
            dispose();
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        } finally {
            btnSave.setEnabled(true);
        }
    }
}
