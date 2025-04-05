package io.github.clagomess.tomato.dto.data;

import lombok.*;

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
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Request {
        private File filepath;
        private RequestDto staging;
    }
}
