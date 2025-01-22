package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.WorkspaceRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RequestNameTextField extends JPanel {
    private final JLabel parent = new JLabel(){{
        setForeground(Color.decode("#616365"));
    }};
    private final JLabel lblRequestName = new JLabel();
    private final WorkspaceRepository workspaceRepository = new WorkspaceRepository();

    private final List<UUID> listenerUuid = new ArrayList<>(0);
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public RequestNameTextField(){
        setLayout(new MigLayout("insets 0 0 0 0"));

        add(parent, "width ::250");
        add(new JLabel(" / "){{
            setForeground(Color.decode("#616365"));
        }});
        add(lblRequestName, "width ::100% - 271px");
    }

    public void setText(
            RequestHeadDto requestHeadDto,
            RequestDto requestDto
    ){
        if(requestHeadDto != null && requestHeadDto.getParent() != null){
            parent.setText(requestHeadDto.getParent().getFlattenedParentString());
        }else{
            try {
                parent.setText(workspaceRepository.getDataSessionWorkspace().getName());
            } catch (Throwable e) {
                new ExceptionDialog(this, e);
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
