package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
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
    private String name = "New Environment";
    private List<KeyValueItemDto> envs = new ArrayList<>();
}
