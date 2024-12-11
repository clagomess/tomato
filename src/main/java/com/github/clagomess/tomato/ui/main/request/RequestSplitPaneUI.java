package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.ui.main.request.left.RequestTabContentUI;
import com.github.clagomess.tomato.ui.main.request.right.ResponseTabContent;
import lombok.Getter;

import javax.swing.*;

@Getter
public class RequestSplitPaneUI extends JSplitPane {
    private final TabKey key;
    private final RequestTabContentUI requestContent;
    private final ResponseTabContent responseContent;

    public RequestSplitPaneUI(
            RequestHeadDto requestHead,
            RequestDto request
    ) {
        super(JSplitPane.HORIZONTAL_SPLIT);

        this.key = new TabKey(request.getId());
        this.responseContent = new ResponseTabContent();
        this.requestContent = new RequestTabContentUI(
                this.key,
                requestHead,
                request,
                responseContent
        );

        setLeftComponent(requestContent);
        setRightComponent(responseContent);
    }
}
