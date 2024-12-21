package com.github.clagomess.tomato.service.http;

import com.github.clagomess.tomato.dto.data.RequestDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlEncodedFormBodyTest {
    @Test
    public void build() {

        var form = List.of(
                new RequestDto.KeyValueItem(TEXT, "myparam", "myvalue", true),
                new RequestDto.KeyValueItem(TEXT, "utf8param", "AçãoAçucar", true),
                new RequestDto.KeyValueItem(TEXT, "nullparam", null, true),
                new RequestDto.KeyValueItem(TEXT, "hidden", "hidden", false),
                new RequestDto.KeyValueItem(TEXT, null, null, true),
                new RequestDto.KeyValueItem(TEXT,  " ", null, true)
        );

        var multipart = new UrlEncodedFormBody(form);
        var result = multipart.build();

        assertEquals(
                "myparam=myvalue&utf8param=A%C3%A7%C3%A3oA%C3%A7ucar&nullparam=",
                result.toString()
        );
    }
}
