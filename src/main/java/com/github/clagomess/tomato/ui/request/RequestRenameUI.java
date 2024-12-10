package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.service.RequestDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.NameUI;

import java.awt.*;

public class RequestRenameUI extends NameUI {
    private final RequestDataService requestDataService = RequestDataService.getInstance();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public RequestRenameUI(
            Component parent,
            CollectionTreeDto.Request requestHead
    ) {
        super(parent);
        setTitle("Rename Request");
        txtName.setText(requestHead.getName());
        btnSave.addActionListener(l -> btnSaveAction(requestHead));
    }

    private void btnSaveAction(CollectionTreeDto.Request requestHead){
        btnSave.setEnabled(false);

        try {
            RequestDto requestDto = requestDataService.load(requestHead)
                    .orElseThrow();

            requestDto.setName(this.txtName.getText());
            requestHead.setName(this.txtName.getText());

            requestDataService.save(
                    requestHead.getPath(),
                    requestDto
            );

            requestPublisher.getOnSave().publish(
                    requestHead.getId(),
                    requestHead
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
