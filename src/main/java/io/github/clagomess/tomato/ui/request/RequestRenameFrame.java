package io.github.clagomess.tomato.ui.request;

import io.github.clagomess.tomato.controller.request.RequestRenameFrameController;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.ui.component.NameFrame;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class RequestRenameFrame extends NameFrame {
    private final RequestRenameFrameController controller = new RequestRenameFrameController(this);

    public RequestRenameFrame(
            Component parent,
            @NonNull RequestHeadDto requestHead
    ) {
        super(parent);

        setTitle("Rename Request");
        txtName.setText(requestHead.getName());
        btnSave.addActionListener(l -> btnSaveAction(requestHead));
    }

    private void btnSaveAction(RequestHeadDto requestHead){
        new WaitExecution(
                this,
                btnSave,
                () -> {
                    controller.save(requestHead);
                    setVisible(false);
                    dispose();
                }
        ).execute();
    }
}
