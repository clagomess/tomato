package com.github.clagomess.tomato.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import lombok.Data;

import java.io.File;
import java.util.stream.Stream;

@Data
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
    }
}
