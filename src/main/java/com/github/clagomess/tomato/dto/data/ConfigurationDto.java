package com.github.clagomess.tomato.dto.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConfigurationDto extends MetadataDto {
    private File dataDirectory;
}
