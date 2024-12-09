package com.github.clagomess.tomato.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import lombok.Data;

import java.io.File;
import java.util.stream.Stream;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionTreeDto {
    private String id;
    private String name;
    private File path;
    private Stream<Request> requests;

    private CollectionTreeDto parent;
    private Stream<CollectionTreeDto> children;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Request {
        private String id;
        private HttpMethodEnum method;
        private String name;
        private File path;
        private CollectionTreeDto parent;
    }

    public Stream<CollectionTreeDto> flattened() {
        return Stream.concat(
                Stream.of(this),
                children.flatMap(CollectionTreeDto::flattened)
        );
    }

    public String flattenedParentString() {
        if(parent == null) return name;
        return parent.flattenedParentString() + " / " + name;
    }
}
