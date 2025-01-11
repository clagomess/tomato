package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"path"})
public class WorkspaceDto extends MetadataDto {
    private String name;

    @JsonIgnore
    private File path;
}
