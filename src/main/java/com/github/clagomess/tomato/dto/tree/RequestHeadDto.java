package com.github.clagomess.tomato.dto.tree;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestHeadDto {
    private String id;
    private HttpMethodEnum method;
    private String name;
    private File path;
    private CollectionTreeDto parent;
}
