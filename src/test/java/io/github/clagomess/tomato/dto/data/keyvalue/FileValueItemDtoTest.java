package io.github.clagomess.tomato.dto.data.keyvalue;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileValueItemDtoTest {
    @Test
    public void equalsHashCode(){
        Assertions.assertThat(new FileKeyValueItemDto())
                .isEqualTo(new FileKeyValueItemDto());
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
        var result = new FileKeyValueItemDto();
        result.setType(inputType);

        assertEquals(expectedType, result.getType());
    }

    @ParameterizedTest
    @CsvSource({
            "TEXT,text/plain,text/plain",
            "TEXT,text/xml,text/xml",
            "TEXT, ,text/plain",
            "FILE,text/xml,text/xml",
            "FILE, ,application/octet-stream",
    })
    public void getValueContentType(
            KeyValueTypeEnum type,
            String inputValueContentType,
            String expectedValueContentType
    ){
        var result = new FileKeyValueItemDto();
        result.setType(type);
        result.setValueContentType(inputValueContentType);

        assertEquals(expectedValueContentType, result.getValueContentType());
    }
}
