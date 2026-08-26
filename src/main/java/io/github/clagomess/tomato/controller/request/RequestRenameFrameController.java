package io.github.clagomess.tomato.controller.request;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import io.github.clagomess.tomato.ui.component.NameInterface;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.io.IOException;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;

@RequiredArgsConstructor
public class RequestRenameFrameController {
    private final RequestRepository requestRepository;
    private final NameInterface ui;

    public RequestRenameFrameController(NameInterface ui) {
        this.ui = ui;
        this.requestRepository = new RequestRepository();
    }

    public void save(
            @NonNull RequestHeadDto requestHead
    ) throws IOException {
        RequestDto requestDto = requestRepository.load(requestHead)
                .orElseThrow();

        requestDto.setName(ui.getTxtNameValue());
        requestHead.setName(ui.getTxtNameValue());

        requestRepository.save(
                requestHead.getPath(),
                requestDto
        );

        RequestPublisher.getInstance().getOnChange().publish(
                new RequestKey(requestHead),
                new PublisherEvent<>(UPDATED, requestHead)
        );
    }
}
