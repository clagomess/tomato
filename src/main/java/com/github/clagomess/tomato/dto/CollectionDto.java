package com.github.clagomess.tomato.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CollectionDto extends MetadataDto {
    private String name;
}
