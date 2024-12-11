package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkspaceDto extends MetadataDto {
    private String name;

    @JsonIgnore
    private File path;
}
