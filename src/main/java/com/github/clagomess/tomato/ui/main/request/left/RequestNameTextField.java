package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.service.WorkspaceDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RequestNameTextField extends JPanel {
    private final JLabel parent = new JLabel();
    private final JLabel lblRequestName = new JLabel();
    private final WorkspaceDataService workspaceDataService = WorkspaceDataService.getInstance();

    private final List<UUID> listenerUuid = new ArrayList<>(0);
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public RequestNameTextField(){
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(parent);
        add(new JLabel(" / "));
        add(lblRequestName);
    }

    public void setText(
            RequestHeadDto requestHeadDto,
            RequestDto requestDto
    ){
        if(requestHeadDto != null && requestHeadDto.getParent() != null){
            parent.setText(requestHeadDto.getParent().getFlattenedParentString());
        }else{
            try {
                parent.setText(workspaceDataService.getDataSessionWorkspace().getName());
            } catch (Throwable e) {
                DialogFactory.createDialogException(this, e);
            }
        }

        lblRequestName.setText(requestDto.getName());

        listenerUuid.add(requestPublisher.getOnSave().addListener(requestDto.getId(), event -> {
            parent.setText(event.getParent().getFlattenedParentString());
            lblRequestName.setText(event.getName());
        }));
    }

    public void dispose(){
        listenerUuid.forEach(uuid -> {
            requestPublisher.getOnSave().removeListener(uuid);
        });
    }
}
