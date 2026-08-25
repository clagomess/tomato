package io.github.clagomess.tomato.ui.request;

import io.github.clagomess.tomato.controller.request.RequestPropertiesFrameController;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.ui.BaseFrame;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;

public class RequestPropertiesFrame extends BaseFrame {
    private final RequestPropertiesFrameController controller;

    public RequestPropertiesFrame(
            Component parent,
            @NonNull RequestHeadDto requestHead
    ) {
        this(parent, requestHead, new RequestPropertiesFrameController());
    }

    protected RequestPropertiesFrame(
            Component parent,
            @NonNull RequestHeadDto requestHead,
            RequestPropertiesFrameController controller
    ) {
        this.controller = controller;

        setTitle("Request Properties");
        setResizable(false);

        setLayout(new MigLayout(
                "insets 10",
                "[][grow, fill]"
        ));

        var properties = this.controller.properties(requestHead);

        addProperty("ID", properties.id());
        addProperty("Name", properties.name());
        addProperty("File", properties.fileName());
        addProperty("Size", properties.fileSize());
        addProperty("Last Modified", properties.fileLastModified());

        var btnClose = new JButton("Close");
        btnClose.addActionListener(l -> dispose());
        add(btnClose, "span 2, align right");

        getRootPane().setDefaultButton(btnClose);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void addProperty(String label, String value){
        add(new JLabel(label));

        var txtValue = new JTextField(value);
        txtValue.setEditable(false);
        txtValue.setCaretPosition(0);
        add(txtValue, "width 400!, wrap");
    }
}
