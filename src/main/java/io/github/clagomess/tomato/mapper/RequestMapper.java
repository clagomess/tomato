package io.github.clagomess.tomato.mapper;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.io.File;

@Mapper
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(target = "path", ignore = true)
    @Mapping(target = "parent", ignore = true)
    void toRequestHead(
            @MappingTarget RequestHeadDto target,
            RequestDto source
    );

    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "method", source = "request.method")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "path", source = "path")
    @Mapping(target = "parent", source = "parent")
    RequestHeadDto toRequestHead(
            RequestDto request,
            CollectionTreeDto parent,
            File path
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    RequestDto duplicate(RequestDto source);
}
