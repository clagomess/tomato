package com.github.clagomess.tomato.mapper;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.external.PostmanCollectionV210Dto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PostmanCollectionPumpMapperTest {
    private final PostmanCollectionPumpMapper pumpMapper = PostmanCollectionPumpMapper.INSTANCE;

    @Test
    public void body_mode_raw(){
        var body = new PostmanCollectionV210Dto.Item.Request.Body();
        body.setMode("raw");
        body.setRaw("xxx");
        body.setOptions(new PostmanCollectionV210Dto.Item.Request.Body.Options());
        body.getOptions().setRaw(new PostmanCollectionV210Dto.Item.Request.Body.Options.Raw());
        body.getOptions().getRaw().setLanguage("json");

        RequestDto.Body result = pumpMapper.map(body);

        assertEquals(BodyTypeEnum.RAW, result.getType());
        assertEquals("application/json", result.getRaw().getType());
        assertEquals(body.getRaw(), result.getRaw().getRaw());
    }

    @Test
    public void formdata_file(){
        var item = new PostmanCollectionV210Dto.Item.Request.Body.FormData();
        item.setKey("file");
        item.setType("file");
        item.setSrc("/home/claudio/Imagens/youtube-en-VTV.jpg");

        RequestDto.KeyValueItem result = pumpMapper.map(item);
        assertEquals(item.getKey(), result.getKey());
        assertEquals(KeyValueTypeEnum.FILE, result.getType());
        assertEquals(item.getSrc(), result.getValue());
    }

    @Test
    public void formdata_disabled(){
        var item = new PostmanCollectionV210Dto.Item.Request.Body.FormData();
        item.setType("text");
        item.setKey("grant_type");
        item.setSrc("password");
        item.setDisabled(true);

        RequestDto.KeyValueItem result = pumpMapper.map(item);
        assertEquals(item.getKey(), result.getKey());
        assertEquals(KeyValueTypeEnum.TEXT, result.getType());
        assertEquals(item.getSrc(), result.getValue());
        assertFalse(result.isSelected());
    }

    @Test
    public void urlencoded_disabled(){
        var item = new PostmanCollectionV210Dto.Item.Request.Body.UrlEncoded();
        item.setType("text");
        item.setKey("grant_type");
        item.setValue("password");
        item.setDisabled(true);

        RequestDto.KeyValueItem result = pumpMapper.map(item);
        assertEquals(item.getKey(), result.getKey());
        assertEquals(KeyValueTypeEnum.TEXT, result.getType());
        assertEquals(item.getValue(), result.getValue());
        assertFalse(result.isSelected());
    }

    @Test
    public void header_disabled(){
        var item = new PostmanCollectionV210Dto.Item.Request.Header();
        item.setKey("content-type");
        item.setValue("json");
        item.setDisabled(true);

        RequestDto.KeyValueItem result = pumpMapper.map(item);
        assertEquals(item.getKey(), result.getKey());
        assertEquals(KeyValueTypeEnum.TEXT, result.getType());
        assertEquals(item.getValue(), result.getValue());
        assertFalse(result.isSelected());
    }
}
