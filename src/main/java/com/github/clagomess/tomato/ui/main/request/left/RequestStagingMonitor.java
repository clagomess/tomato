package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.component.StagingMonitor;

public class RequestStagingMonitor extends StagingMonitor<RequestDto> {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    private final TabKey tabKey;

    public RequestStagingMonitor(
            TabKey tabKey,
            RequestHeadDto requestHeadDto,
            RequestDto dto
    ) {
        super(dto);
        this.tabKey = tabKey;

        if(requestHeadDto == null) {
            this.currentHashCode = 0;
        }
    }

    @Override
    public void reset(){
        super.reset();

        requestPublisher.getOnStaging().publish(tabKey, false);
    }

    @Override
    public void update(){
        super.update();

        requestPublisher.getOnStaging().publish(
                tabKey,
                isDiferent()
        );
    }
}
