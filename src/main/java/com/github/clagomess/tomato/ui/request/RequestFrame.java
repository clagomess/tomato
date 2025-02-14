package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.controller.request.RequestFrameController;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.ui.MainFrame;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import com.github.clagomess.tomato.ui.main.request.RequestSplitPaneUI;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class RequestFrame extends JFrame {
    private final RequestFrameController controller = new RequestFrameController();

    public RequestFrame(
            @Nullable RequestHeadDto requestHead,
            @Nullable RequestDto request
    ) throws IOException {
        RequestDto loadedRequest = controller.load(requestHead, request);

        setTitle(loadedRequest.getName());
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(1000, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new MigLayout(
                "insets 5",
                "[grow,fill]"
        ));

        var requestSplitPaneUI = new RequestSplitPaneUI(requestHead, loadedRequest);
        controller.getDispose().add(requestSplitPaneUI::dispose);

        add(requestSplitPaneUI, "height 100%");

        controller.addOnStagingListener(
                requestSplitPaneUI.getKey(),
                loadedRequest,
                this::setTitle
        );

        controller.addOnChangeListener(requestHead, this::setTitle);

        if(requestSplitPaneUI.getRequestStagingMonitor().isDiferent()) {
            setTitle("[*] " + loadedRequest.getName());
        }

        pack();
        setLocationRelativeTo(
                Arrays.stream(Window.getWindows())
                        .filter(item -> item instanceof MainFrame)
                        .findFirst()
                        .orElse(null)
        );
        setVisible(true);
    }

    @Override
    public void dispose() {
        controller.dispose();
        super.dispose();
    }
}
