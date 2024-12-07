package com.github.clagomess.tomato.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class DataSessionDto extends MetadataDto {
    private String workspaceId;
}
