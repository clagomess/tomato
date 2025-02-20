package com.github.clagomess.tomato.mapper;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.request.BodyDto;
import com.github.clagomess.tomato.dto.external.PostmanCollectionV210Dto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

@Mapper
public interface PostmanCollectionDumpMapper {
    PostmanCollectionDumpMapper INSTANCE = Mappers.getMapper(PostmanCollectionDumpMapper.class);

    @Mapping(target = "request", ignore = true)
    @Mapping(target = "item", ignore = true)
    PostmanCollectionV210Dto.Item toItem(CollectionTreeDto source);

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "request.auth", ignore = true)
    @Mapping(target = "request.method", source = "method")
    @Mapping(target = "request.url.raw", source = "url")
    @Mapping(target = "request.url.host", source = "url", qualifiedByName = "parseUrlHost")
    @Mapping(target = "request.url.path", source = "url", qualifiedByName = "parseUrlPath")
    @Mapping(target = "request.url.query", source = "urlParam.query")
    @Mapping(target = "request.url.variable", source = "urlParam.path")
    @Mapping(target = "request.header", source = "headers")
    @Mapping(target = "request.body", source = "body")
    PostmanCollectionV210Dto.Item toItem(RequestDto source);

    @AfterMapping
    default void toItemAfter(
            @MappingTarget PostmanCollectionV210Dto.Item target,
            RequestDto source
    ){
        if(source.getBody().getType() == BodyTypeEnum.NO_BODY){
            target.getRequest().setBody(null);
        }

        if(target.getRequest().getUrl().getQuery().isEmpty()){
            target.getRequest().getUrl().setQuery(null);
        }

        if(target.getRequest().getUrl().getVariable().isEmpty()){
            target.getRequest().getUrl().setVariable(null);
        }
    }

    @Mapping(target = "urlencoded", source = "urlEncodedForm")
    @Mapping(target = "mode", source = "type")
    @Mapping(target = "formdata", source = "multiPartForm")
    @Mapping(target = "options.raw.language", source = "raw.type")
    @Mapping(target = "raw", source = "raw.raw")
    PostmanCollectionV210Dto.Item.Request.Body toBody(BodyDto source);

    @AfterMapping
    default void toBodyAfter(
            @MappingTarget PostmanCollectionV210Dto.Item.Request.Body target,
            BodyDto source
    ){
        switch (source.getType()){
            case RAW: target.setMode("raw"); break;
            case BINARY: target.setMode("file"); break;
            case MULTIPART_FORM: target.setMode("formdata"); break;
            case URL_ENCODED_FORM: target.setMode("urlencoded"); break;
        }
    }

    @AfterMapping
    default void mapAfter(
            @MappingTarget PostmanCollectionV210Dto.Item.Request.Body.UrlEncoded target,
            ContentTypeKeyValueItemDto source
    ){
        target.setDisabled(!source.isSelected());
    }

    @AfterMapping
    default void mapAfter(
            @MappingTarget PostmanCollectionV210Dto.Item.Request.Body.FormData target,
            FileKeyValueItemDto source
    ){
        target.setDisabled(!source.isSelected());
        target.setType(source.getType().name().toLowerCase());
    }

    @AfterMapping
    default void mapAfter(
            @MappingTarget PostmanCollectionV210Dto.Item.Request.Header target,
            KeyValueItemDto source
    ){
        target.setDisabled(!source.isSelected());
    }

    @AfterMapping
    default void mapAfter(
            @MappingTarget PostmanCollectionV210Dto.Item.Request.Url.Param target,
            KeyValueItemDto source
    ){
        target.setDisabled(!source.isSelected());
    }

    @Named("parseUrlHost")
    static List<String> parseUrlHost(String url){
        if(StringUtils.isBlank(url)) return List.of("");

        if(url.startsWith("http:")){
            int beginIndex = url.indexOf("://") + 3;
            String[] result = url.substring(beginIndex).split("/");

            if(result.length == 0){
                return List.of(url);
            }else{
                return List.of(url.substring(0, beginIndex) + result[0]);
            }
        }

        if(url.contains("/")){
            return List.of(url.substring(0, url.indexOf("/")));
        }else{
            return List.of(url);
        }
    }

    @Named("parseUrlPath")
    static List<String> parseUrlPath(String url){
        if(StringUtils.isBlank(url)) return List.of();

        String path = url.substring(parseUrlHost(url).get(0).length());

        if(path.contains("/")) {
            return Arrays.stream(path.split("/"))
                    .filter(StringUtils::isNotBlank)
                    .toList();
        }else{
            return List.of();
        }
    }
}
