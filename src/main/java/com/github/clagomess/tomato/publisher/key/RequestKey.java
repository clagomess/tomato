package com.github.clagomess.tomato.publisher.key;

import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RequestKey {
    private final String parentCollectionId;
    private final String requestId;

    public RequestKey(RequestHeadDto requestHead) {
        this.parentCollectionId = requestHead.getParent().getId();
        this.requestId = requestHead.getId();
    }
}
