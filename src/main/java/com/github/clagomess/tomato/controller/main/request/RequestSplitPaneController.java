package com.github.clagomess.tomato.controller.main.request;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.http.HttpService;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.mapper.RequestMapper;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;

@RequiredArgsConstructor
public class RequestSplitPaneController {
    private final RequestRepository requestRepository;

    public RequestSplitPaneController() {
        this.requestRepository = new RequestRepository();
    }

    public Thread sendRequest(
            RequestDto request,
            OnSendRequestCompleteFI onComplete,
            OnSendRequestErrorFI onError
    ) {
        var httpService = new HttpService(request);

        Thread requestThread = new Thread(() -> {
            try {
                ResponseDto response = httpService.perform();
                onComplete.update(response);
            } catch (Throwable e) {
                onError.update(e);
                onComplete.update(null);
            }
        }, "request-perform");

        requestThread.start();

        return requestThread;
    }

    public void save(RequestHeadDto requestHead, RequestDto request) throws IOException {
        requestRepository.save(requestHead.getPath(), request);

        RequestMapper.INSTANCE.toRequestHead(
                requestHead,
                request
        );

        RequestPublisher.getInstance().getOnChange().publish(
                new RequestKey(requestHead),
                new PublisherEvent<>(UPDATED, requestHead)
        );
    }

    @FunctionalInterface
    public interface OnSendRequestCompleteFI {
        void update(@Nullable ResponseDto response);
    }

    @FunctionalInterface
    public interface OnSendRequestErrorFI {
        void update(Throwable throwable);
    }
}
