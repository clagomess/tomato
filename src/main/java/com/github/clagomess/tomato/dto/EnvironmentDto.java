package com.github.clagomess.tomato.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class EnvironmentDto {
    private String id = UUID.randomUUID().toString();
    private String name;
    private List<Env> envs = new ArrayList<>();

    @Data
    public static class Env {
        private String id = UUID.randomUUID().toString();
        private String key;
        private String value;
    }
}
