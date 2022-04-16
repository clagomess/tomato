package com.github.clagomess.tomato.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CollectionDto {
    private String id = UUID.randomUUID().toString();
    private String name;
    private List<RequestDto> requests = new ArrayList<>();
}
