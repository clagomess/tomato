package io.github.clagomess.tomato.controller.main.request;

import io.github.clagomess.tomato.dto.ResponseDto;
import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.http.HttpService;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.mapper.RequestMapper;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;

@RequiredArgsConstructor
public class RequestSplitPaneController {
    private final RequestRepository requestRepository;

    public RequestSplitPaneController() {
        this.requestRepository = new RequestRepository();
    }

    public boolean isProductionEnvironment() throws IOException {
        Optional<EnvironmentDto> current = new EnvironmentRepository()
                .getWorkspaceSessionEnvironment();

        return current.map(EnvironmentDto::isProduction)
                .orElse(false);
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
            } catch (Exception e) {
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
        void update(Exception throwable);
    }
}
