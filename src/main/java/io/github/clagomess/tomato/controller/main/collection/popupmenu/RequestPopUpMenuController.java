package io.github.clagomess.tomato.controller.main.collection.popupmenu;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.mapper.RequestMapper;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;

@RequiredArgsConstructor
public class RequestPopUpMenuController {
    private final RequestRepository requestRepository;

    public RequestPopUpMenuController() {
        this.requestRepository = new RequestRepository();
    }

    public RequestDto load(RequestHeadDto requestHead) throws IOException {
        return requestRepository.load(requestHead)
                .orElseThrow();
    }

    public void duplicate(RequestHeadDto requestHead) throws IOException {
        var request = requestRepository.load(requestHead)
                .map(RequestMapper.INSTANCE::duplicate)
                .orElseThrow();
        request.setName(request.getName() + " Copy");

        var filePath = requestRepository.save(
                requestHead.getParent().getPath(),
                request
        );

        var newRequestHead = RequestMapper.INSTANCE.toRequestHead(
                request,
                requestHead.getParent(),
                filePath
        );

        RequestPublisher.getInstance()
                .getOnChange()
                .publish(
                        new RequestKey(newRequestHead),
                        new PublisherEvent<>(INSERTED, newRequestHead)
                );
    }
}
