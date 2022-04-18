package com.github.clagomess.tomato.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EnvironmentDto extends TomatoMetadataDto {
    private String name;
    private List<Env> envs = new ArrayList<>();

    public EnvironmentDto(String name) {
        this.name = name;
    }

    @Data
    public static class Env extends TomatoMetadataDto {
        private String key;
        private String value;
    }

    @Override
    public String toString() {
        return name;
    }
}
