package com.github.clagomess.tomato.dto.data;

import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyValueItemDtoTest {
    @Test
    public void equalsHashCode(){
        Assertions.assertThat(new KeyValueItemDto())
                .isEqualTo(new KeyValueItemDto());
    }

    @Test
    public void sort(){
        List<KeyValueItemDto> list = new ArrayList<>(2);
        list.add(new KeyValueItemDto("bbb", "value"));
        list.add(new KeyValueItemDto("aaa", "value"));

        Collections.sort(list);

        assertEquals("aaa", list.get(0).getKey());
    }

    @ParameterizedTest
    @CsvSource({
            ",TEXT",
            "TEXT,TEXT",
            "FILE,FILE"
    })
    public void getType(
            KeyValueTypeEnum inputType,
            KeyValueTypeEnum expectedType
    ){
        var result = new KeyValueItemDto();
        result.setType(inputType);

        assertEquals(expectedType, result.getType());
    }

    @ParameterizedTest
    @CsvSource({
            "TEXT,text/plain,text/plain",
            "TEXT,xx,text/plain",
            "TEXT, ,text/plain",
            "FILE,text/xml,text/xml",
            "FILE, ,application/octet-stream",
    })
    public void getValueContentType(
            KeyValueTypeEnum type,
            String inputValueContentType,
            String expectedValueContentType
    ){
        var result = new KeyValueItemDto();
        result.setType(type);
        result.setValueContentType(inputValueContentType);

        assertEquals(expectedValueContentType, result.getValueContentType());
    }
}
