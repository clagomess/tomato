package com.github.clagomess.tomato.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BodyTypeEnum {
    NO_BODY("No Body"),
    MULTIPART_FORM("Multipart Form"),
    URL_ENCODED_FORM("URL Encoded Form"),
    RAW("Raw"),
    BINARY("Binary");

    private final String description;

    @Override
    public String toString() {
        return description;
    }
}
