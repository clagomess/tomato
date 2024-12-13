package com.github.clagomess.tomato.mapper;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.io.File;
import java.util.Collections;
import java.util.List;

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

    default MultivaluedMap<String, Object> toMultivaluedMap(
            List<RequestDto.KeyValueItem> source
    ){
        if(source == null || source.isEmpty()) return new MultivaluedHashMap<>();

        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>(source.size());
        source.forEach(item -> {
            map.put(item.getKey(), Collections.singletonList(item.getValue()));
        });

        return map;
    }

    default Form toForm(
            List<RequestDto.KeyValueItem> source
    ) {
        if(source == null || source.isEmpty()) return new Form();

        Form form = new Form();
        source.forEach(item -> {
            form.param(item.getKey(), item.getValue());
        });

        return form;
    }

    default FormDataMultiPart toFormDataMultiPart(
            List<RequestDto.KeyValueItem> source
    ){
        if(source == null || source.isEmpty()) return new FormDataMultiPart();

        FormDataMultiPart form = new FormDataMultiPart();
        source.forEach(item -> form.field(item.getKey(), item.getValue()));
        return form;
    }
}
