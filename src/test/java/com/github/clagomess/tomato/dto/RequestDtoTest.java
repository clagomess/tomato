package com.github.clagomess.tomato.dto;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class RequestDtoTest {
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
        body.getMultiPartForm().add(new RequestDto.MultiPartFormItem("foo", "bar"));

        val form = body.toMultiPartForm();

        Assertions.assertEquals("bar", form.getField("foo").getValue());
    }

    @Test
    public void Body_toUrlEncodedForm(){
        RequestDto.Body body = new RequestDto.Body();
        body.setUrlEncodedForm(new ArrayList<>());
        body.getUrlEncodedForm().add(new RequestDto.MultiPartFormItem("foo", "bar"));

        val form = body.toUrlEncodedForm();

        Assertions.assertEquals("bar", form.get("foo").get(0));
    }
}
