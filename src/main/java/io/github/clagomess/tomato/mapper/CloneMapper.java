package io.github.clagomess.tomato.mapper;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

@Mapper(mappingControl = DeepClone.class)
public interface CloneMapper {
    CloneMapper INSTANCE = Mappers.getMapper(CloneMapper.class);

    EnvironmentDto clone(EnvironmentDto source);

    EnvironmentItemDto clone(EnvironmentItemDto source);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "path", ignore = true)
    RequestHeadDto clone(RequestHeadDto source);
}
