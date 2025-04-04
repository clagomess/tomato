package com.github.clagomess.tomato.mapper;

import com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EnvironmentItemMapper {
    EnvironmentItemMapper INSTANCE = Mappers.getMapper(EnvironmentItemMapper.class);

    EnvironmentItemDto clone(EnvironmentItemDto source);
}
