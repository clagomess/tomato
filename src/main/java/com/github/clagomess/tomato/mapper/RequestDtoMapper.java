package com.github.clagomess.tomato.mapper;

import com.github.clagomess.tomato.dto.RequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RequestDtoMapper {
    RequestDtoMapper INSTANCE = Mappers.getMapper(RequestDtoMapper.class);

    void copy(@MappingTarget RequestDto target, RequestDto source);
}
