package com.github.clagomess.tomato.mapper;

import com.github.clagomess.tomato.dto.data.RequestDto;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMapperTest {
    private final RequestMapper mapper = RequestMapper.INSTANCE;

    @Test
    public void toMultivaluedMap(){
        List<RequestDto.KeyValueItem> list = new ArrayList<>();
        list.add(new RequestDto.KeyValueItem("foo", "bar"));

        var result = mapper.toMultivaluedMap(list);

        assertEquals("bar", result.get("foo").get(0));
    }

    @Test
    public void toForm(){
        List<RequestDto.KeyValueItem> list = new ArrayList<>();
        list.add(new RequestDto.KeyValueItem("foo", "bar"));

        var result = mapper.toForm(list);

        assertEquals("bar", result.asMap().get("foo").get(0));
    }

    @Test
    public void toFormDataMultiPart() throws IOException {
        List<RequestDto.KeyValueItem> list = new ArrayList<>();
        list.add(new RequestDto.KeyValueItem("foo", "bar"));

        try(var result = mapper.toFormDataMultiPart(list)) {
            assertEquals(
                    "bar",
                    result.getField("foo").getValue()
            );
        }
    }
}
