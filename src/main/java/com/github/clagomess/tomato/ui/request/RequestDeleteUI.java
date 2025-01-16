package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;

@RequiredArgsConstructor
public class RequestDeleteUI {
    private final Component parent;
    private final RequestHeadDto requestHead;

    private final RequestRepository requestRepository = new RequestRepository();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

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
            delete();
        }
    }

    private void delete(){
        new WaitExecution(parent, () -> {
            requestRepository.delete(requestHead);

            // update source collection
            var key = new RequestPublisher.RequestKey(
                    requestHead.getParent().getId(),
                    requestHead.getId()
            );

            requestPublisher.getOnDelete().publish(
                    key,
                    new RequestPublisher.RequestId(requestHead.getId())
            );
        }).execute();
    }
}
