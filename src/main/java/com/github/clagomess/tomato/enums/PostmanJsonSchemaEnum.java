package com.github.clagomess.tomato.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostmanJsonSchemaEnum {
    COLLECTION("postman.collection.v2.1.0.schema.json");

    private final String resourceFile;
}
