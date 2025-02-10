package com.github.clagomess.tomato.mapper;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EnvironmentMapper {
    EnvironmentMapper INSTANCE = Mappers.getMapper(EnvironmentMapper.class);

    EnvironmentDto clone(EnvironmentDto source);
}
