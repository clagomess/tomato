package com.github.clagomess.tomato.controller.main.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.publisher.base.EventTypeEnum;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RequestTabbedPaneController {
    private final RequestRepository requestRepository;

    public RequestTabbedPaneController() {
        this.requestRepository = new RequestRepository();
    }

    public void addWorkspaceOnSwitchListener(Runnable runnable) {
        WorkspacePublisher.getInstance()
                .getOnSwitch()
                .addListener(event -> {
                    runnable.run();
                });
    }

    public void addRequestOnLoadListener(OnLoadFI runnable) {
        RequestPublisher.getInstance()
                .getOnLoad()
                .addListener(e -> loadRequest(e, runnable));
    }

    protected void loadRequest(
            PublisherEvent<RequestHeadDto> event,
            OnLoadFI runnable
    ) {
        try {
            if (event.getType().equals(EventTypeEnum.NEW)) {
                runnable.load(event.getEvent(), new RequestDto());
            } else {
                var request = requestRepository.load(event.getEvent()).orElseThrow();
                runnable.load(event.getEvent(), request);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public interface OnLoadFI {
        void load(
                RequestHeadDto requestHead,
                RequestDto request
        );
    }
}
