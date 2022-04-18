package com.github.clagomess.tomato.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WorkspaceDto extends TomatoMetadataDto {
    private String name;
    private List<CollectionDto> collections = new ArrayList<>();
    private List<EnvironmentDto> environments = new ArrayList<>();
}
