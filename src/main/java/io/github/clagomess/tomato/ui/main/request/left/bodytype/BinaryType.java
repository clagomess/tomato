package io.github.clagomess.tomato.ui.main.request.left.bodytype;

import io.github.clagomess.tomato.dto.data.request.BinaryBodyDto;
import io.github.clagomess.tomato.ui.component.FileChooser;
import io.github.clagomess.tomato.ui.component.ListenableTextField;
import io.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class BinaryType extends JPanel {
    private final ListenableTextField contentType = new ListenableTextField();
    private final FileChooser fileChooser = new FileChooser();

    public BinaryType(
            BinaryBodyDto binaryBody,
            RequestStagingMonitor requestStagingMonitor
    ){
        setLayout(new MigLayout(
                "insets 5 2 2 2",
                "[grow, fill]"
        ));

        contentType.setText(binaryBody.getContentType());
        contentType.addOnChange(value -> {
            binaryBody.setContentType(value);
            requestStagingMonitor.update();
        });

        fileChooser.setValue(binaryBody.getFile());
        fileChooser.addOnChange(value -> {
            binaryBody.setFile(value != null ? value.getAbsolutePath() : null);

            try {
                var result = value != null ?
                        Files.probeContentType(value.toPath()) :
                        null;

                binaryBody.setContentType(result);
                contentType.setText(StringUtils.stripToEmpty(result));
            }catch(IOException e){
                log.warn(e.getMessage());
            }

            requestStagingMonitor.update();
        });

        add(new JLabel("Content-Type:"), "wrap");
        add(contentType, "wrap");
        add(new JLabel("File:"), "wrap");
        add(fileChooser, "wrap");
    }
}
