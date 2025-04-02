package com.github.clagomess.tomato.mapper;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import com.github.clagomess.tomato.dto.external.PostmanEnvironmentDto;
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
    PostmanEnvironmentDto.Value map(EnvironmentItemDto source);

    @AfterMapping
    default void map(
            @MappingTarget PostmanEnvironmentDto.Value target
    ) {
        target.setEnabled(true);
    }
}
