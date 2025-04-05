package io.github.clagomess.tomato.ui.main.request.left;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.ui.component.StagingMonitor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
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
            return;
        }

        try {
            new RequestRepository().load(requestHeadDto)
                    .ifPresent(item -> this.currentHashCode = item.hashCode());
        }catch (IOException e){
            log.error(e.getMessage(), e);
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
