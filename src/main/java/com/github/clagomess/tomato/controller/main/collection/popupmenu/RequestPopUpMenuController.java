package com.github.clagomess.tomato.controller.main.collection.popupmenu;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.mapper.RequestMapper;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;

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
