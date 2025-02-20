package com.github.clagomess.tomato.mapper;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum;
import com.github.clagomess.tomato.dto.data.request.BodyDto;
import com.github.clagomess.tomato.dto.external.PostmanCollectionV210Dto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

@Mapper
public interface PostmanCollectionPumpMapper {
    PostmanCollectionPumpMapper INSTANCE = Mappers.getMapper(PostmanCollectionPumpMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    CollectionDto toCollectionDto(PostmanCollectionV210Dto.Item source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "url", source = "request.url.raw")
    @Mapping(target = "urlParam.charset", ignore = true)
    @Mapping(target = "urlParam.query", source = "request.url.query")
    @Mapping(target = "urlParam.path", source = "request.url.variable")
    @Mapping(target = "method", source = "request.method")
    @Mapping(target = "headers", source = "request.header")
    @Mapping(target = "cookies", ignore = true)
    @Mapping(target = "body", source = "request.body")
    RequestDto toRequestDto(PostmanCollectionV210Dto.Item source);

    @AfterMapping
    default void toRequestDtoAfter(
            @MappingTarget RequestDto target,
            PostmanCollectionV210Dto.Item source
    ){
        if(source.getRequest().getAuth() != null && "bearer".equals(source.getRequest().getAuth().getType())){
            if(target.getHeaders() == null){
                target.setHeaders(new ArrayList<>());
            }

            source.getRequest().getAuth().getBearer().forEach(item ->
                target.getHeaders().add(new KeyValueItemDto(
                        "Authorization",
                        "Bearer " + item.getValue()
                ))
            );
        }
    }

    @Mapping(target = "selected", ignore = true)
    @Mapping(target = "valueContentType", ignore = true)
    ContentTypeKeyValueItemDto map(PostmanCollectionV210Dto.Item.Request.Url.Param source);

    @AfterMapping
    default void mapAfter(
            @MappingTarget KeyValueItemDto target,
            PostmanCollectionV210Dto.Item.Request.Url.Param source
    ){
        target.setSelected(source.getDisabled() == null || !source.getDisabled());
    }

    @Mapping(target = "selected", ignore = true)
    KeyValueItemDto map(PostmanCollectionV210Dto.Item.Request.Header source);

    @AfterMapping
    default void mapAfter(
            @MappingTarget KeyValueItemDto target,
            PostmanCollectionV210Dto.Item.Request.Header source
    ){
        target.setSelected(source.getDisabled() == null || !source.getDisabled());
    }

    @Mapping(target = "type", source = "mode")
    @Mapping(target = "charset", ignore = true)
    @Mapping(target = "raw.raw", source = "raw")
    @Mapping(target = "binary", ignore = true)
    @Mapping(target = "urlEncodedForm", source = "urlencoded")
    @Mapping(target = "multiPartForm", source = "formdata")
    BodyDto map(PostmanCollectionV210Dto.Item.Request.Body source);

    @AfterMapping
    default void mapAfter(
            @MappingTarget BodyDto target,
            PostmanCollectionV210Dto.Item.Request.Body source
    ){
        if(target.getRaw() != null &&
                source.getOptions() != null &&
                source.getOptions().getRaw() != null) {
            if("json".equals(source.getOptions().getRaw().getLanguage())){
                target.getRaw().setType(RawBodyTypeEnum.JSON);
            }
        }
    }

    default BodyTypeEnum map(String source){
        if("raw".equals(source)) return BodyTypeEnum.RAW;
        if("graphql".equals(source)) return BodyTypeEnum.RAW;
        if("file".equals(source)) return BodyTypeEnum.BINARY;
        if("formdata".equals(source)) return BodyTypeEnum.MULTIPART_FORM;
        if("urlencoded".equals(source)) return BodyTypeEnum.URL_ENCODED_FORM;

        return BodyTypeEnum.NO_BODY;
    }

    @Mapping(target = "selected", ignore = true)
    @Mapping(target = "valueContentType", ignore = true)
    ContentTypeKeyValueItemDto map(PostmanCollectionV210Dto.Item.Request.Body.UrlEncoded source);

    @AfterMapping
    default void mapAfter(
            @MappingTarget ContentTypeKeyValueItemDto target,
            PostmanCollectionV210Dto.Item.Request.Body.UrlEncoded source
    ){
        target.setSelected(source.getDisabled() == null || !source.getDisabled());
    }

    @Mapping(target = "value", ignore = true)
    @Mapping(target = "selected", ignore = true)
    @Mapping(target = "valueContentType", ignore = true)
    FileKeyValueItemDto map(PostmanCollectionV210Dto.Item.Request.Body.FormData source);

    @AfterMapping
    default void mapAfter(
            @MappingTarget FileKeyValueItemDto target,
            PostmanCollectionV210Dto.Item.Request.Body.FormData source
    ){
        target.setSelected(source.getDisabled() == null || !source.getDisabled());

        if("file".equals(source.getType())){
            target.setValue(source.getSrc());
        }else{
            target.setValue(source.getValue());
        }
    }

    default KeyValueTypeEnum mapKeyType(String source){
        if("file".equals(source)) return KeyValueTypeEnum.FILE;
        return KeyValueTypeEnum.TEXT;
    }
}
