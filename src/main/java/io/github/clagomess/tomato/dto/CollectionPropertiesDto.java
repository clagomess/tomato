package io.github.clagomess.tomato.dto;

public record CollectionPropertiesDto(
        String id,
        String name,
        String fileName,
        String fileSize,
        String fileCount,
        String fileLastModified
) {
}
