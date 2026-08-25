package io.github.clagomess.tomato.controller.request;

import io.github.clagomess.tomato.dto.RequestPropertiesDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RequestPropertiesFrameController {
    private final RequestRepository requestRepository;

    public RequestPropertiesFrameController() {
        this.requestRepository = new RequestRepository();
    }

    public RequestPropertiesDto properties(RequestHeadDto requestHead) {
        return requestRepository.properties(requestHead);
    }
}
