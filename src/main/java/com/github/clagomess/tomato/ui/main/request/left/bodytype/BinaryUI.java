package com.github.clagomess.tomato.ui.main.request.left.bodytype;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.ui.component.FileChooser;
import com.github.clagomess.tomato.ui.component.ListenableTextField;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class BinaryUI extends JPanel implements BodyTypeUI {
    private final ListenableTextField contentType = new ListenableTextField();
    private final FileChooser fileChooser = new FileChooser();

    public BinaryUI(
            RequestDto.BinaryBody binaryBody,
            RequestStagingMonitor requestStagingMonitor
    ){
        setLayout(new MigLayout("insets 0 0 0 0", "[grow, fill]", ""));

        contentType.setText(binaryBody.getContentType());
        contentType.addOnChange(value -> {
            binaryBody.setContentType(value);
            requestStagingMonitor.update();
        });

        fileChooser.setValue(binaryBody.getFile());
        fileChooser.addOnChange(value -> {
            binaryBody.setFile(value != null ? value.getAbsolutePath() : null);
            requestStagingMonitor.update();
        });

        add(new JLabel("Content-Type:"), "wrap");
        add(contentType, "wrap");
        add(new JLabel("File:"), "wrap");
        add(fileChooser, "wrap");
    }
}
