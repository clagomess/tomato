package io.github.clagomess.tomato.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthTypeEnum {
    NO_AUTH("No Auth"),
    BASIC("Basic");

    private final String description;

    @Override
    public String toString() {
        return description;
    }
}
