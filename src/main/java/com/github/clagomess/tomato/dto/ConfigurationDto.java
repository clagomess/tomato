package com.github.clagomess.tomato.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConfigurationDto extends MetadataDto {
    private File dataDirectory;
}
