package io.github.clagomess.tomato.dto.data;

import lombok.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class WorkspaceSessionDto extends MetadataDto {
    private TomatoID environmentId;
    private List<Request> requests = new LinkedList<>();
    private List<TomatoID> expandedCollectionsIds = new ArrayList<>();
    private File lastOpenedDirectory;

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
