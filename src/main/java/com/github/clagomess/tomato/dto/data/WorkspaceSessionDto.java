package com.github.clagomess.tomato.dto.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class WorkspaceSessionDto extends MetadataDto {
    private String environmentId;
    private List<Request> requests = new LinkedList<>();

    public List<Request> getRequests() {
        if(requests == null) requests = new LinkedList<>();
        return requests;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Request {
        @NotNull
        private File filepath;

        @Nullable
        private RequestDto staging;
    }
}
