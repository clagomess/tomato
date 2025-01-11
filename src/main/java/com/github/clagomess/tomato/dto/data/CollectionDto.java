package com.github.clagomess.tomato.dto.data;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CollectionDto extends MetadataDto {
    private String name;
}
