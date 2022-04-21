package com.github.clagomess.tomato.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EnvironmentDto extends TomatoMetadataDto {
    private String name = "New Environment";
    private List<Env> envs = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class Env extends TomatoMetadataDto {
        private String key;
        private String value;
    }

    @Override
    public String toString() {
        return name;
    }
}
