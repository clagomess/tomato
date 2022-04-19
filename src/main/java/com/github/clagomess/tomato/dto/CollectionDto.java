package com.github.clagomess.tomato.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CollectionDto extends TomatoMetadataDto {
    private String name;

    @JsonIgnore
    private List<RequestDto> requests = new ArrayList<>();

    public CollectionDto(String name, List<RequestDto> requests) {
        this.name = name;
        this.requests = requests;
    }
}
