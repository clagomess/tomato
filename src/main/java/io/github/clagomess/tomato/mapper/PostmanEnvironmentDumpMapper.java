package io.github.clagomess.tomato.mapper;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.external.PostmanEnvironmentDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostmanEnvironmentDumpMapper {
    PostmanEnvironmentDumpMapper INSTANCE = Mappers.getMapper(PostmanEnvironmentDumpMapper.class);

    @Mapping(target = "values", source = "envs")
    PostmanEnvironmentDto toEnvironmentDto(EnvironmentDto source);

    @Mapping(target = "enabled", ignore = true)
    PostmanEnvironmentDto.Value map(KeyValueItemDto source);

    @AfterMapping
    default void map(
            @MappingTarget PostmanEnvironmentDto.Value target
    ) {
        target.setEnabled(true);
    }
}
