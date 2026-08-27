package io.github.clagomess.tomato.controller.request;

import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.exception.TomatoException;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.mapper.CloneMapper;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;
import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;

@RequiredArgsConstructor
public class RequestMoveFrameController {
    private final CloneMapper cloneMapper = CloneMapper.INSTANCE;
    private final RequestRepository requestRepository;
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public RequestMoveFrameController() {
        this.requestRepository = new RequestRepository();
    }

    public void moveRequest(
            RequestHeadDto requestHead,
            CollectionTreeDto destination
    ) throws IOException {
        if(destination == null) throw new TomatoException("Destination not selected");

        // move request
        requestRepository.move(requestHead, destination);

        // update source collection
        requestPublisher.getOnChange().publish(
                new RequestKey(requestHead),
                new PublisherEvent<>(DELETED, requestHead)
        );

        // update target collection
        var newRequestHead = cloneMapper.clone(requestHead);
        newRequestHead.setParent(destination);
        newRequestHead.setPath(new File(
                destination.getPath(),
                requestHead.getPath().getName()
        ));

        requestPublisher.getOnChange().publish(
                new RequestKey(newRequestHead),
                new PublisherEvent<>(INSERTED, newRequestHead)
        );
    }
}
