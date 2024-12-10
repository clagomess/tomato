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
    private CollectionTreeDto parent;
    private BuildStreamFI<CollectionTreeDto> children = parent -> Stream.empty();
    private BuildStreamFI<Request> requests = parent -> Stream.empty();

    public Stream<CollectionTreeDto> getChildren() {
        return children.build(this);
    }

    public Stream<Request> getRequests() {
        return requests.build(this);
    }

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
                getChildren().flatMap(CollectionTreeDto::flattened)
        );
    }

    public String flattenedParentString() {
        if(parent == null) return name;
        return parent.flattenedParentString() + " / " + name;
    }

    @FunctionalInterface
    public interface BuildStreamFI<T> {
        Stream<T> build(CollectionTreeDto parent);
    }
}
