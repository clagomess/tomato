package com.github.clagomess.tomato.dto.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EnvironmentDto extends MetadataDto {
    private String name = "New Environment";
    private List<Env> envs = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Env {
        private String key;
        private String value;
    }
}
