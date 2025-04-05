package io.github.clagomess.tomato.dto.data.keyvalue;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentTypeKeyValueItemDtoTest {
    @Test
    public void equalsHashCode(){
        Assertions.assertThat(new ContentTypeKeyValueItemDto())
                .isEqualTo(new ContentTypeKeyValueItemDto());
    }

    @ParameterizedTest
    @CsvSource({
            "text/plain,text/plain",
            "text/xml,text/xml",
            " ,text/plain",
    })
    public void getValueContentType(
            String inputValueContentType,
            String expectedValueContentType
    ){
        var result = new ContentTypeKeyValueItemDto();
        result.setValueContentType(inputValueContentType);

        assertEquals(expectedValueContentType, result.getValueContentType());
    }
}
