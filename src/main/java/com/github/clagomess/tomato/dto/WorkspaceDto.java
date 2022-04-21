package com.github.clagomess.tomato.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkspaceDto extends TomatoMetadataDto {
    private String name;

    @JsonIgnore
    private List<CollectionDto> collections = new ArrayList<>();

    @JsonIgnore
    private List<EnvironmentDto> environments = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
