package io.github.clagomess.tomato.mapper;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.external.PostmanEnvironmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostmanEnvironmentPumpMapper {
    PostmanEnvironmentPumpMapper INSTANCE = Mappers.getMapper(PostmanEnvironmentPumpMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "envs", source = "values")
    @Mapping(target = "production", ignore = true)
    EnvironmentDto toEnvironmentDto(PostmanEnvironmentDto source);

    @Mapping(target = "secretId", ignore = true)
    @Mapping(target = "type", ignore = true)
    EnvironmentItemDto map(PostmanEnvironmentDto.Value source);
}
