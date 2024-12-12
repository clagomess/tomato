package com.github.clagomess.tomato.mapper;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.external.PostmanCollectionV210Dto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PumpMapper {
    PumpMapper INSTANCE = Mappers.getMapper(PumpMapper.class);

    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    CollectionDto toCollectionDto(PostmanCollectionV210Dto.Item source);


    @Mapping(target = "url", source = "request.url.raw")
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "method", source = "request.method")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "headers", source = "request.header")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "cookies", ignore = true) //@TODO: check
    @Mapping(target = "body", source = "request.body")
    RequestDto toRequestDto(PostmanCollectionV210Dto.Item source);

    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "selected", ignore = true) //@TODO: check
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    RequestDto.KeyValueItem map(PostmanCollectionV210Dto.Item.Request.Header source);

    @Mapping(target = "bodyType", source = "mode")
    @Mapping(target = "bodyContentType", ignore = true)
    @Mapping(target = "binaryFilePath", ignore = true) //@TODO: check
    @Mapping(target = "urlEncodedForm", source = "urlencoded")
    @Mapping(target = "multiPartForm", source = "formdata")
    RequestDto.Body map(PostmanCollectionV210Dto.Item.Request.Body source);

    default BodyTypeEnum map(String source){
        if("raw".equals(source)) return BodyTypeEnum.RAW;
        if("formdata".equals(source)) return BodyTypeEnum.MULTIPART_FORM;
        if("urlencoded".equals(source)) return BodyTypeEnum.URL_ENCODED_FORM;

        throw new RuntimeException("Not implemented: " + source);
    }

    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "selected", ignore = true) //@TODO: check
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    RequestDto.KeyValueItem map(PostmanCollectionV210Dto.Item.Request.Body.UrlEncoded source);

    @Mapping(target = "value", source = "src")
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "selected", ignore = true) //@TODO: check
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    RequestDto.MultiPartFormItem map(PostmanCollectionV210Dto.Item.Request.Body.FormData source);

    default KeyValueTypeEnum mapKeyType(String source){
        if("file".equals(source)) return KeyValueTypeEnum.FILE;
        throw new RuntimeException("Not implemented: " + source);
    }
}
