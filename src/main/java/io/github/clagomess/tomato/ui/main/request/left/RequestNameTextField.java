package io.github.clagomess.tomato.ui.main.request.left;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import io.github.clagomess.tomato.ui.component.ColorConstant;
import io.github.clagomess.tomato.ui.component.ExceptionDialog;
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
        lblRequestName.setText(request.getName());

        if(requestHead != null && requestHead.getParent() != null){
            parent.setText(requestHead.getParent().getFlattenedParentString());
        }else{
            try {
                parent.setText(workspaceRepository.getDataSessionWorkspace().getName());
            } catch (Exception e) {
                new ExceptionDialog(this, e);
            }
        }

        RequestKey key = new RequestKey(requestHead, request);

        listenerUuid = requestPublisher.getOnChange().addListener(key, event -> {
            parent.setText(event.getEvent().getParent().getFlattenedParentString());
            lblRequestName.setText(event.getEvent().getName());
            request.setName(event.getEvent().getName());
        });
    }

    public void dispose(){
        requestPublisher.getOnChange().removeListener(listenerUuid);
    }
}
