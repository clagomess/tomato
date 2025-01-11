package com.github.clagomess.tomato.dto.data;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class ConfigurationDto extends MetadataDto {
    private File dataDirectory;
}
