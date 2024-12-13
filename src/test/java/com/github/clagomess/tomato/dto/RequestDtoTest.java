package com.github.clagomess.tomato.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

@Slf4j
public class RequestDtoTest {
    private RequestDto getFilledDto(){
        RequestDto dto = new RequestDto();
        dto.setName(RandomStringUtils.randomAlphabetic(10));
        dto.setUrl("https://" + RandomStringUtils.randomAlphabetic(10));
        dto.getHeaders().add(new RequestDto.KeyValueItem(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10)));
        dto.getCookies().add(new RequestDto.KeyValueItem(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10)));
//        dto.getBody().setBodyContentType(RandomStringUtils.randomAlphabetic(10)); //@TODO: check
//        dto.getBody().setRaw(RandomStringUtils.randomAlphabetic(10)); //@TODO: check
//        dto.getBody().setBinaryFilePath(RandomStringUtils.randomAlphabetic(10)); // @TODO: check
        dto.getBody().setUrlEncodedForm(new ArrayList<>());
        dto.getBody().getUrlEncodedForm().add(new RequestDto.KeyValueItem(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10)));
        dto.getBody().setMultiPartForm(new ArrayList<>());
        dto.getBody().getMultiPartForm().add(new RequestDto.KeyValueItem(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10)));

        return dto;
    }

    @Test
    public void toJson() throws JsonProcessingException {
        String val = ObjectMapperUtil.getInstance().writeValueAsString(getFilledDto());
        log.info("{}", val);
    }

    @Test
    public void toMultivaluedMapHeaders(){
        RequestDto dto = new RequestDto();
        dto.getHeaders().add(new RequestDto.KeyValueItem("foo", "bar"));

        val map = dto.toMultivaluedMapHeaders();

        Assertions.assertEquals("bar", map.get("foo").get(0));
    }

    @Test
    public void Body_toMultiPartForm(){
        RequestDto.Body body = new RequestDto.Body();
        body.setMultiPartForm(new ArrayList<>());
        body.getMultiPartForm().add(new RequestDto.KeyValueItem("foo", "bar"));

        val form = body.toMultiPartForm();

        Assertions.assertEquals("bar", form.getField("foo").getValue());
    }

    @Test
    public void Body_toUrlEncodedForm(){
        RequestDto.Body body = new RequestDto.Body();
        body.setUrlEncodedForm(new ArrayList<>());
        body.getUrlEncodedForm().add(new RequestDto.KeyValueItem("foo", "bar"));

        val form = body.toUrlEncodedForm();

        Assertions.assertEquals("bar", form.get("foo").get(0));
    }
}
