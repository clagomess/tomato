package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;

public class RequestStagingMonitor {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final TabKey tabKey;
    private int currentHashCode;
    private int actualHashCode;

    public RequestStagingMonitor(
            TabKey tabKey,
            RequestHeadDto requestHeadDto,
            RequestDto dto
    ) {
        this.tabKey = tabKey;
        this.currentHashCode = requestHeadDto == null ? 0 : dto.hashCode();
        this.actualHashCode = dto.hashCode();
    }

    public void reset(RequestDto dto){
        this.currentHashCode = dto.hashCode();
        this.actualHashCode = dto.hashCode();
        requestPublisher.getOnStaging().publish(tabKey, false);
    }

    public void setActualHashCode(RequestDto dto){
        this.actualHashCode = dto.hashCode();
        requestPublisher.getOnStaging().publish(
                tabKey,
                currentHashCode != actualHashCode
        );
    }
}
