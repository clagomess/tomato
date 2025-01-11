package com.github.clagomess.tomato.dto.data;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EnvironmentDto extends MetadataDto {
    private String name = "New Environment";
    private List<Env> envs = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Env {
        private String key;
        private String value;
    }
}
