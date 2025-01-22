package com.github.clagomess.tomato.mapper;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.external.PostmanEnvironmentDto;
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
    EnvironmentDto toEnvironmentDto(PostmanEnvironmentDto source);

    EnvironmentDto.Env map(PostmanEnvironmentDto.Value source);
}
