package com.github.clagomess.tomato.dto.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class WorkspaceSessionDto extends MetadataDto {
    private String environmentId;
}
