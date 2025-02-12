package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import com.github.clagomess.tomato.ui.MainUI;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import com.github.clagomess.tomato.ui.main.request.RequestSplitPaneUI;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class RequestUI extends JFrame {
    private final List<Runnable> dispose = new ArrayList<>(3);

    protected RequestUI(){}

    public RequestUI(
            RequestHeadDto requestHead
    ) throws IOException {
        setTitle(requestHead.getName());
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(1000, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new MigLayout(
                "insets 5",
                "[grow,fill]"
        ));

        RequestDto requestDto = new RequestRepository().load(requestHead).orElseThrow();
        var requestSplitPaneUI = new RequestSplitPaneUI(requestHead, requestDto);
        dispose.add(requestSplitPaneUI::dispose);

        add(requestSplitPaneUI, "height 100%");

        addOnChangeListener(requestHead);
        addOnStagingListener(requestSplitPaneUI.getKey(), requestHead);

        pack();
        setLocationRelativeTo(
                Arrays.stream(Window.getWindows())
                        .filter(item -> item instanceof MainUI)
                        .findFirst()
                        .orElse(null)
        );
        setVisible(true);
    }

    protected void addOnStagingListener(
            TabKey tabKey,
            RequestHeadDto requestHead
    ){
        RequestPublisher.getInstance()
                .getOnStaging()
                .addListener(tabKey, changed -> {
                    if(changed){
                        setTitle("[*] " + requestHead.getName());
                    }else{
                        setTitle(requestHead.getName());
                    }
                });

        dispose.add(() -> RequestPublisher.getInstance()
                .getOnStaging()
                .removeListener(tabKey));
    }

    protected void addOnChangeListener(RequestHeadDto requestHead){
        var key = new RequestKey(requestHead);

        RequestPublisher.getInstance().getOnChange().addListener(
                key,
                event -> setTitle(event.getEvent().getName())
        );

        dispose.add(() -> RequestPublisher.getInstance()
                .getOnChange()
                .removeListener(key));
    }

    @Override
    public void dispose() {
        dispose.forEach(Runnable::run);
        super.dispose();
    }
}
