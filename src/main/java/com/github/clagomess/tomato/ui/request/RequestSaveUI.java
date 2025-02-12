package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.mapper.RequestMapper;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import com.github.clagomess.tomato.ui.collection.CollectionComboBox;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;

public class RequestSaveUI extends JFrame {
    private final RequestDto requestDto;
    private final OnSaveFI onSaveListener;
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();
    private final CollectionComboBox cbCollection = new CollectionComboBox(null);

    private final RequestRepository requestRepository = new RequestRepository();
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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("Request Name"), "wrap");
        add(txtName, "width 300!, wrap");
        add(new JLabel("Collection"), "wrap");
        add(cbCollection, "width 300!, wrap");
        add(btnSave, "align right");

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

            var filePath = requestRepository.save(
                    parent.getPath(),
                    requestDto
            );

            RequestHeadDto requestHead = RequestMapper.INSTANCE.toRequestHead(
                    requestDto,
                    parent,
                    filePath
            );

            requestPublisher.getOnChange().publish(
                    new RequestKey(requestHead),
                    new PublisherEvent<>(INSERTED, requestHead)
            );
            onSaveListener.response(requestHead);

            setVisible(false);
            dispose();
        }).execute();
    }

    @FunctionalInterface
    public interface OnSaveFI {
        void response(RequestHeadDto requestHead);
    }
}
