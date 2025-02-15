package com.github.clagomess.tomato.controller.main.request;

import com.github.clagomess.tomato.dto.RequestTabSnapshotDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.SystemPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.publisher.base.EventTypeEnum;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RequestTabbedPaneController {
    private final RequestRepository requestRepository;
    private final WorkspaceSessionRepository workspaceSessionRepository;

    public RequestTabbedPaneController() {
        this.requestRepository = new RequestRepository();
        this.workspaceSessionRepository = new WorkspaceSessionRepository();
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

    public void addSaveRequestsSnapshotListener(RequestTabSnapshotFI snapshot) {
        WorkspacePublisher.getInstance()
                .getOnBeforeSwitch()
                .addListener(e -> saveRequestsSnapshot(snapshot.get()));

        SystemPublisher.getInstance()
                .getOnClosing()
                .addListener(e -> saveRequestsSnapshot(snapshot.get()));
    }

    protected void saveRequestsSnapshot(List<RequestTabSnapshotDto> itens) {
        try {
            var requestSessionState = itens.stream()
                    .map(RequestTabSnapshotDto::toSessionState)
                    .toList();

            var session = workspaceSessionRepository.load();
            session.setRequests(requestSessionState);
            workspaceSessionRepository.save(session);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    // @TODO: impl. load session

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

    @FunctionalInterface
    public interface OnLoadFI {
        void load(
                RequestHeadDto requestHead,
                RequestDto request
        );
    }

    @FunctionalInterface
    public interface RequestTabSnapshotFI {
        List<RequestTabSnapshotDto> get();
    }
}
