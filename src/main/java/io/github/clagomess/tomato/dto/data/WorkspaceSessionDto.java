package io.github.clagomess.tomato.dto.data;

import lombok.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class WorkspaceSessionDto extends MetadataDto {
    private String environmentId;
    private List<Request> requests = new LinkedList<>();
    private List<String> expandedCollectionsIds = new ArrayList<>();

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
        private String filepath;
        private RequestDto staging;
    }
}
