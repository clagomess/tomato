package com.github.clagomess.tomato.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkspaceDto extends MetadataDto {
    private String name;
}
