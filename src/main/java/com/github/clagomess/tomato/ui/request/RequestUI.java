package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import com.github.clagomess.tomato.ui.main.request.RequestSplitPaneUI;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

@Slf4j
public class RequestUI extends JFrame {
    private final RequestRepository requestRepository = new RequestRepository();
    private final RequestSplitPaneUI requestSplitPaneUI;

    public RequestUI(
            RequestHeadDto requestHead
    ) throws IOException, InterruptedException {
        // @TODO: add title listener when change name
        // @TODO: add '*' when change request
        setTitle(requestHead.getName());
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(600, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new MigLayout(
                "debug, insets 0 10 5 5"
        ));

        RequestDto requestDto = requestRepository.load(requestHead).orElseThrow();
        requestSplitPaneUI = new RequestSplitPaneUI(requestHead, requestDto);
        panel.add(requestSplitPaneUI);

        add(panel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void dispose() {
        requestSplitPaneUI.dispose();
        super.dispose();
    }
}
