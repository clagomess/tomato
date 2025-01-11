package com.github.clagomess.tomato.dto.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class WorkspaceSessionDto extends MetadataDto {
    private String environmentId;
}
