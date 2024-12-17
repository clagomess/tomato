package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.service.RequestDataService;
import com.github.clagomess.tomato.ui.component.NameUI;
import com.github.clagomess.tomato.ui.component.WaitExecution;

import java.awt.*;

public class RequestRenameUI extends NameUI {
    private final RequestDataService requestDataService = new RequestDataService();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public RequestRenameUI(
            Component parent,
            RequestHeadDto requestHead
    ) {
        super(parent);
        setTitle("Rename Request");
        txtName.setText(requestHead.getName());
        btnSave.addActionListener(l -> btnSaveAction(requestHead));
    }

    private void btnSaveAction(RequestHeadDto requestHead){
        new WaitExecution(this, btnSave).setExecute(() -> {
            RequestDto requestDto = requestDataService.load(requestHead)
                    .orElseThrow();

            requestDto.setName(this.txtName.getText());
            requestHead.setName(this.txtName.getText());

            requestDataService.save(
                    requestHead.getPath(),
                    requestDto
            );

            var key = new RequestPublisher.RequestKey(
                    requestHead.getParent().getId(),
                    requestHead.getId()
            );
            requestPublisher.getOnUpdate().publish(key, requestHead);
            requestPublisher.getOnSave().publish(requestHead.getId(), requestHead);

            setVisible(false);
            dispose();
        }).execute();
    }
}
