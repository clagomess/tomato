package io.github.clagomess.tomato.ui.request;

import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;

@RequiredArgsConstructor
public class RequestDeleteFrame {
    private final Component parent;
    private final RequestHeadDto requestHead;

    public void showConfirmDialog(){
        int ret = JOptionPane.showConfirmDialog(
                parent,
                String.format(
                        "Are you sure you want to delete \"%s\"?",
                        requestHead.getName()
                ),
                "Request Delete",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if(ret == JOptionPane.OK_OPTION){
            new WaitExecution(parent, this::delete).execute();
        }
    }

    protected void delete() throws IOException {
        new RequestRepository().delete(requestHead);

        RequestPublisher.getInstance().getOnChange().publish(
                new RequestKey(requestHead),
                new PublisherEvent<>(DELETED, requestHead)
        );
    }
}
