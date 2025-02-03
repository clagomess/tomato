package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.ui.MainUI;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import com.github.clagomess.tomato.ui.main.request.RequestSplitPaneUI;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class RequestUI extends JFrame {
    private final RequestRepository requestRepository = new RequestRepository();
    private final RequestSplitPaneUI requestSplitPaneUI;

    public RequestUI(
            RequestHeadDto requestHead
    ) throws IOException {
        // @TODO: add title listener when change name
        // @TODO: add '*' when change request
        setTitle(requestHead.getName());
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(600, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new MigLayout(
                "insets 5",
                "[grow,fill]"
        ));

        RequestDto requestDto = requestRepository.load(requestHead).orElseThrow();
        requestSplitPaneUI = new RequestSplitPaneUI(requestHead, requestDto);
        add(requestSplitPaneUI, "height 100%");

        pack();
        setLocationRelativeTo(
                Arrays.stream(Window.getWindows())
                        .filter(item -> item instanceof MainUI)
                        .findFirst()
                        .orElse(null)
        );
        setVisible(true);
    }

    @Override
    public void dispose() {
        requestSplitPaneUI.dispose();
        super.dispose();
    }
}
