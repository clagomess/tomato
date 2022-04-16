package com.github.clagomess.tomato.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class WorkspaceDto {
    private String id = UUID.randomUUID().toString();
    private String name;
    private List<CollectionDto> collections = new ArrayList<>();
    private List<EnvironmentDto> environments = new ArrayList<>();
}
