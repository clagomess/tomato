package com.github.clagomess.tomato.dto.data;

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
public class EnvironmentDto extends MetadataDto {
    private String name = "New Environment";
    private List<KeyValueItemDto> envs = new ArrayList<>();
}
