package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class WorkspaceDto extends MetadataDto {
    private String name;

    @JsonIgnore
    private File path;
}
