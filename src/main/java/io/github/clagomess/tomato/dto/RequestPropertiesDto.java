package io.github.clagomess.tomato.dto;

public record RequestPropertiesDto(
        String id,
        String name,
        String fileName,
        String fileSize,
        String fileLastModified
) {
}
