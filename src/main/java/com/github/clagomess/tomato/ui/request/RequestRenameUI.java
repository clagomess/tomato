package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import com.github.clagomess.tomato.ui.component.NameUI;
import com.github.clagomess.tomato.ui.component.WaitExecution;

import java.awt.*;
import java.io.IOException;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;

public class RequestRenameUI extends NameUI {
    protected RequestRepository requestRepository;

    public RequestRenameUI(
            Component parent,
            RequestHeadDto requestHead
    ) {
        super(parent);
        this.requestRepository = new RequestRepository();

        setTitle("Rename Request");
        txtName.setText(requestHead.getName());
        btnSave.addActionListener(l -> btnSaveAction(requestHead));
    }

    private void btnSaveAction(RequestHeadDto requestHead){
        new WaitExecution(
                this,
                btnSave,
                () -> save(requestHead)
        ).execute();
    }

    protected void save(RequestHeadDto requestHead) throws IOException {
        RequestDto requestDto = requestRepository.load(requestHead)
                .orElseThrow();

        requestDto.setName(getTxtName().getText());
        requestHead.setName(getTxtName().getText());

        requestRepository.save(
                requestHead.getPath(),
                requestDto
        );

        RequestPublisher.getInstance().getOnChange().publish(
                new RequestKey(requestHead),
                new PublisherEvent<>(UPDATED, requestHead)
        );

        setVisible(false);
        dispose();
    }
}
