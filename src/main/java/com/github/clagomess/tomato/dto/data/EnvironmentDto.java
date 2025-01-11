package com.github.clagomess.tomato.dto.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EnvironmentDto extends MetadataDto {
    private String name = "New Environment";
    private List<Env> envs = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Env {
        private String key;
        private String value;
    }
}
