package io.github.clagomess.tomato.ui.request;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import io.github.clagomess.tomato.ui.component.NameFrame;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;

public class RequestRenameFrame extends NameFrame {
    protected RequestRepository requestRepository;

    public RequestRenameFrame(
            Component parent,
            @NotNull RequestHeadDto requestHead
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
