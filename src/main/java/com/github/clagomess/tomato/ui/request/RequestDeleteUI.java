package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.Repository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;

@RequiredArgsConstructor
public class RequestDeleteUI {
    private final Component parent;
    private final RequestHeadDto requestHead;

    private final Repository repository = new Repository();
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
            // @TODO: change to own *repository and apply cache evict
            repository.delete(requestHead.getPath());

            // update source collection
            requestPublisher.getOnMove().publish(
                    new RequestPublisher.ParentCollectionId(
                            requestHead.getParent().getId()
                    ),
                    new RequestPublisher.RequestId(requestHead.getId())
            );
        }).execute();
    }
}
