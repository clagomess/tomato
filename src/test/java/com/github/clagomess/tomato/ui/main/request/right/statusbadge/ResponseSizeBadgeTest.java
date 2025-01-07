package com.github.clagomess.tomato.ui.main.request.right.statusbadge;

import com.github.clagomess.tomato.dto.ResponseDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseSizeBadgeTest {
    @ParameterizedTest
    @CsvSource({
            "100,100B",
            "10240,10.00KB",
            "2048576,1.95MB",
    })
    public void formatSize(
            Long size,
            String expected
    ){
        var response = Mockito.mock(ResponseDto.Response.class);
        Mockito.when(response.getBodySize()).thenReturn(size);

        var ui = new ResponseSizeBadge(response);
        assertEquals(expected, ui.formatSize(size));
    }
}
