package com.github.clagomess.tomato.dto.tree;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import lombok.Data;

import java.io.File;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestHeadDto {
    private String id;
    private HttpMethodEnum method;
    private String name;
    private File path;
    private CollectionTreeDto parent;
}
