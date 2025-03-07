package com.github.clagomess.tomato.dto.tree;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestHeadDto implements Comparable<RequestHeadDto> {
    private String id;
    private HttpMethodEnum method;
    private String name;
    private File path;
    private CollectionTreeDto parent;

    @Override
    public int compareTo(RequestHeadDto o) {
        return StringUtils.compareIgnoreCase(this.getName(), o.getName());
    }
}
