package com.github.clagomess.tomato.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CollectionDto extends TomatoMetadataDto {
    private String name;
    private List<RequestDto> requests = new ArrayList<>();

    public CollectionDto(String name, List<RequestDto> requests) {
        this.name = name;
        this.requests = requests;
    }
}
