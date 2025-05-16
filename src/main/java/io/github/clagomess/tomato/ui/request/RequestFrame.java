package io.github.clagomess.tomato.ui.request;

import io.github.clagomess.tomato.controller.request.RequestFrameController;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import io.github.clagomess.tomato.ui.main.request.RequestSplitPane;
import io.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static io.github.clagomess.tomato.ui.component.ComponentUtil.getMainWindow;

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

        var key = new TabKey(loadedRequest.getId());
        var requestStagingMonitor = new RequestStagingMonitor(
                key,
                requestHead,
                loadedRequest
        );
        var requestSplitPane = new RequestSplitPane(
                key,
                requestStagingMonitor,
                requestHead,
                loadedRequest
        );

        controller.getDispose().add(requestSplitPane::dispose);

        add(requestSplitPane, "height 100%");

        controller.addOnStagingListener(
                key,
                loadedRequest,
                this::setTitle
        );

        controller.addOnChangeListener(requestHead, this::setTitle);

        if(requestStagingMonitor.isDiferent()) {
            setTitle("[*] " + loadedRequest.getName());
        }

        pack();
        setLocationRelativeTo(getMainWindow());
        setVisible(true);
    }

    @Override
    public void dispose() {
        controller.dispose();
        super.dispose();
    }
}
