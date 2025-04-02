package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentDto extends MetadataDto {
    private static final String DEFAULT_NAME = "New Environment";

    private String name;
    private List<EnvironmentItemDto> envs;
    private boolean production;

    public String getName() {
        if(name == null) name = DEFAULT_NAME;
        return name;
    }

    public List<EnvironmentItemDto> getEnvs() {
        if(envs == null) envs = new ArrayList<>();
        return envs;
    }
}
