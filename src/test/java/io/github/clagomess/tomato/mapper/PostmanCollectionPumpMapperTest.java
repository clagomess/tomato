package io.github.clagomess.tomato.mapper;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum;
import io.github.clagomess.tomato.dto.data.request.BodyDto;
import io.github.clagomess.tomato.dto.external.PostmanCollectionV210Dto;
import io.github.clagomess.tomato.enums.BodyTypeEnum;
import io.github.clagomess.tomato.enums.RawBodyTypeEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PostmanCollectionPumpMapperTest {
    private final PostmanCollectionPumpMapper pumpMapper = PostmanCollectionPumpMapper.INSTANCE;

    @Test
    void urlParam(){
        var param = new PostmanCollectionV210Dto.Item.Request.Url.Param();
        param.setKey("key");
        param.setValue("value");

        var request = new PostmanCollectionV210Dto.Item();
        request.setRequest(new PostmanCollectionV210Dto.Item.Request());
        request.getRequest().setUrl(new PostmanCollectionV210Dto.Item.Request.Url());
        request.getRequest().getUrl().setQuery(List.of(param));
        request.getRequest().getUrl().setVariable(List.of(param));

        RequestDto result = pumpMapper.toRequestDto(request);
        Assertions.assertThat(result.getUrlParam().getPath()).isNotEmpty();
        Assertions.assertThat(result.getUrlParam().getQuery()).isNotEmpty();
    }

    @Test
    void urlParam_disabled(){
        var param = new PostmanCollectionV210Dto.Item.Request.Url.Param();
        param.setKey("seqTransparencia");
        param.setValue("87");
        param.setDisabled(true);

        ContentTypeKeyValueItemDto result = pumpMapper.map(param);

        assertEquals(param.getKey(), result.getKey());
        assertEquals(param.getValue(), result.getValue());
        assertFalse(result.isSelected());
    }

    @Test
    void body_mode_raw(){
        var body = new PostmanCollectionV210Dto.Item.Request.Body();
        body.setMode("raw");
        body.setRaw("xxx");
        body.setOptions(new PostmanCollectionV210Dto.Item.Request.Body.Options());
        body.getOptions().setRaw(new PostmanCollectionV210Dto.Item.Request.Body.Options.Raw());
        body.getOptions().getRaw().setLanguage("json");

        BodyDto result = pumpMapper.map(body);

        assertEquals(BodyTypeEnum.RAW, result.getType());
        assertEquals(RawBodyTypeEnum.JSON, result.getRaw().getType());
        assertEquals(body.getRaw(), result.getRaw().getRaw());
    }

    @Test
    void formdata_file(){
        var item = new PostmanCollectionV210Dto.Item.Request.Body.FormData();
        item.setKey("file");
        item.setType("file");
        item.setSrc("/home/claudio/Imagens/youtube-en-VTV.jpg");

        FileKeyValueItemDto result = pumpMapper.map(item);
        assertEquals(item.getKey(), result.getKey());
        assertEquals(KeyValueTypeEnum.FILE, result.getType());
        assertEquals(item.getSrc(), result.getValue());
    }

    @Test
    void formdata_disabled(){
        var item = new PostmanCollectionV210Dto.Item.Request.Body.FormData();
        item.setType("text");
        item.setKey("grant_type");
        item.setValue("password");
        item.setDisabled(true);

        FileKeyValueItemDto result = pumpMapper.map(item);
        assertEquals(item.getKey(), result.getKey());
        assertEquals(KeyValueTypeEnum.TEXT, result.getType());
        assertEquals(item.getValue(), result.getValue());
        assertFalse(result.isSelected());
    }

    @Test
    void urlencoded_disabled(){
        var item = new PostmanCollectionV210Dto.Item.Request.Body.UrlEncoded();
        item.setType("text");
        item.setKey("grant_type");
        item.setValue("password");
        item.setDisabled(true);

        ContentTypeKeyValueItemDto result = pumpMapper.map(item);
        assertEquals(item.getKey(), result.getKey());
        assertEquals(item.getValue(), result.getValue());
        assertFalse(result.isSelected());
    }

    @Test
    void header_disabled(){
        var item = new PostmanCollectionV210Dto.Item.Request.Header();
        item.setKey("content-type");
        item.setValue("json");
        item.setDisabled(true);

        KeyValueItemDto result = pumpMapper.map(item);
        assertEquals(item.getKey(), result.getKey());
        assertEquals(item.getValue(), result.getValue());
        assertFalse(result.isSelected());
    }

    @Test
    void header_whenAuthBearer_appendToHeader(){
        var bearer = new PostmanCollectionV210Dto.Item.Request.Auth.Bearer();
        bearer.setKey("token");
        bearer.setValue("{{token}}");

        var header = new PostmanCollectionV210Dto.Item.Request.Header();
        header.setKey("content-type");
        header.setValue("json");

        var request = new PostmanCollectionV210Dto.Item();
        request.setRequest(new PostmanCollectionV210Dto.Item.Request());
        request.getRequest().setAuth(new PostmanCollectionV210Dto.Item.Request.Auth());
        request.getRequest().getAuth().setType("bearer");
        request.getRequest().getAuth().setBearer(List.of(bearer));
        request.getRequest().setHeader(List.of(header));

        RequestDto result = pumpMapper.toRequestDto(request);

        Assertions.assertThat(result.getHeaders()).isNotEmpty();
        Assertions.assertThat(result.getHeaders())
                .filteredOn(item -> item.getKey().equalsIgnoreCase("content-type"))
                .anyMatch(item -> item.getValue().equals("json"));
        Assertions.assertThat(result.getHeaders())
                .filteredOn(item -> item.getKey().equalsIgnoreCase("Authorization"))
                .anyMatch(item -> item.getValue().equals("Bearer {{token}}"));
    }
}
