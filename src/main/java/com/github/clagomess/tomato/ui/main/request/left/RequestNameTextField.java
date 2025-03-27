package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.WorkspaceRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import com.github.clagomess.tomato.ui.component.ColorConstant;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.UUID;

public class RequestNameTextField extends JPanel {
    private final JLabel parent = new JLabel(){{
        setForeground(ColorConstant.GRAY);
    }};
    private final JLabel lblRequestName = new JLabel();
    private final WorkspaceRepository workspaceRepository = new WorkspaceRepository();

    private UUID listenerUuid;
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public RequestNameTextField(){
        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[]0[]0[]"
        ));

        add(parent, "width ::250");
        add(new JLabel(" / "){{
            setForeground(ColorConstant.GRAY);
        }});
        add(lblRequestName, "width ::100% - 271px");
    }

    public void setText(
            @Nullable RequestHeadDto requestHead,
            @NotNull RequestDto request
    ){
        if(requestHead != null && requestHead.getParent() != null){
            parent.setText(requestHead.getParent().getFlattenedParentString());

            listenerUuid = requestPublisher.getOnChange().addListener(new RequestKey(requestHead), event -> {
                parent.setText(event.getEvent().getParent().getFlattenedParentString());
                lblRequestName.setText(event.getEvent().getName());
                request.setName(event.getEvent().getName());
            });
        }else{
            try {
                parent.setText(workspaceRepository.getDataSessionWorkspace().getName());
            } catch (Throwable e) {
                new ExceptionDialog(this, e);
            }
        }

        lblRequestName.setText(request.getName());
    }

    public void dispose(){
        requestPublisher.getOnChange().removeListener(listenerUuid);
    }
}
