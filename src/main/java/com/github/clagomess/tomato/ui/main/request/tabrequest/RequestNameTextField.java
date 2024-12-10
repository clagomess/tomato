package com.github.clagomess.tomato.ui.main.request.tabrequest;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.service.WorkspaceDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;

import javax.swing.*;
import java.awt.*;

public class RequestNameTextField extends JPanel {
    private final JLabel parent = new JLabel();
    private final JLabel lblRequestName = new JLabel();
    private final WorkspaceDataService workspaceDataService = WorkspaceDataService.getInstance();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public RequestNameTextField(){
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(parent);
        add(new JLabel(" / "));
        add(lblRequestName);
    }

    public void setText(
            CollectionTreeDto.Request requestHeadDto,
            RequestDto requestDto
    ){
        if(requestHeadDto != null && requestHeadDto.getParent() != null){
            parent.setText(requestHeadDto.getParent().flattenedParentString());
        }else{
            try {
                parent.setText(workspaceDataService.getDataSessionWorkspace().getName());
            } catch (Throwable e) {
                DialogFactory.createDialogException(this, e);
            }
        }

        lblRequestName.setText(requestDto.getName());

        requestPublisher.getOnSave().addListener(requestDto.getId(), event -> {
            parent.setText(event.getParent().flattenedParentString());
            lblRequestName.setText(event.getName());
        });
    }
}
