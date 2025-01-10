package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.mapper.RequestMapper;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.collection.CollectionComboBox;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class RequestSaveUI extends JFrame {
    private final RequestDto requestDto;
    private final OnSaveFI onSaveListener;
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();
    private final CollectionComboBox cbCollection = new CollectionComboBox(null);

    private final RequestRepository requestDataService = new RequestRepository();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public RequestSaveUI(
            Component parent,
            RequestDto dto,
            OnSaveFI onSaveListener
    ){
        this.requestDto = dto;
        this.onSaveListener = onSaveListener;

        setTitle("Save Request");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setResizable(false);

        JPanel panel = new JPanel(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        panel.add(new JLabel("Request Name"), "wrap");
        panel.add(txtName, "width 300!, wrap");
        panel.add(new JLabel("Collection"), "wrap");
        panel.add(cbCollection, "width 300!, wrap");
        panel.add(btnSave, "align right");
        add(panel);

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        txtName.setText(dto.getName());
        btnSave.addActionListener(l -> btnSaveAction());
    }

    private void btnSaveAction(){
        new WaitExecution(this, btnSave, () -> {
            this.requestDto.setName(this.txtName.getText());

            CollectionTreeDto parent = cbCollection.getSelectedItem();
            if(parent == null) throw new Exception("Parent is null");

            var filePath = requestDataService.save(
                    parent.getPath(),
                    requestDto
            );

            RequestHeadDto requestHead = RequestMapper.INSTANCE.toRequestHead(
                    requestDto,
                    parent,
                    filePath
            );

            var key = new RequestPublisher.ParentCollectionId(parent.getId());
            requestPublisher.getOnInsert().publish(key, requestHead);
            requestPublisher.getOnSave().publish(requestHead.getId(), requestHead);
            onSaveListener.reponse(requestHead);

            setVisible(false);
            dispose();
        }).execute();
    }

    @FunctionalInterface
    public interface OnSaveFI {
        void reponse(RequestHeadDto requestHead);
    }
}
