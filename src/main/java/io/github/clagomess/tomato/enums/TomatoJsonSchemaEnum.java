package io.github.clagomess.tomato.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TomatoJsonSchemaEnum {
    COLLECTION("collection.schema.json"),
    CONFIGURATION("configuration.schema.json"),
    DATA_SESSION("data-session.schema.json"),
    ENVIRONMENT("environment.schema.json"),
    REQUEST("request.schema.json"),
    WORKSPACE("workspace.schema.json"),
    WORKSPACE_SESSION("workspace-session.schema.json");

    private final String resourceFile;
}
