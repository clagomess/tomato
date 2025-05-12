package io.github.clagomess.tomato.publisher.key;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@RequiredArgsConstructor
public class RequestKey {
    private final String parentCollectionId;
    private final String requestId;

    public RequestKey(@NotNull RequestHeadDto requestHead) {
        this.parentCollectionId = requestHead.getParent().getId();
        this.requestId = requestHead.getId();
    }

    public RequestKey(
            @Nullable RequestHeadDto requestHead,
            @NotNull RequestDto request
    ){
        if(requestHead != null){
            this.parentCollectionId = requestHead.getParent().getId();
            this.requestId = requestHead.getId();
        }else{
            this.parentCollectionId = null;
            this.requestId = request.getId();
        }
    }
}
