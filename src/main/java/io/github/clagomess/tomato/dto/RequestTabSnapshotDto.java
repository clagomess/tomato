package io.github.clagomess.tomato.dto;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public class RequestTabSnapshotDto {
    private boolean unsaved;

    @Nullable
    private RequestHeadDto requestHead;

    @NotNull
    private RequestDto request;

    public WorkspaceSessionDto.Request toSessionState(){
        // new
        if(requestHead == null){
            return new WorkspaceSessionDto.Request(null, request);
        }

        // modified
        if(unsaved){
            return new WorkspaceSessionDto.Request(requestHead.getPath(), request);
        }

        // opened
        return new WorkspaceSessionDto.Request(requestHead.getPath(), null);
    }
}
