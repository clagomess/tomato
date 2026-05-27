package io.github.clagomess.tomato.publisher.key;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.TomatoID;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Data
@RequiredArgsConstructor
public class RequestKey {
    private final TomatoID parentCollectionId;
    private final TomatoID requestId;

    public RequestKey(@NonNull RequestHeadDto requestHead) {
        this.parentCollectionId = requestHead.getParent().getId();
        this.requestId = requestHead.getId();
    }

    public RequestKey(
            @Nullable RequestHeadDto requestHead,
            @NonNull RequestDto request
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
