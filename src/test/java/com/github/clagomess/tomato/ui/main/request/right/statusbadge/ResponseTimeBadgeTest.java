package com.github.clagomess.tomato.ui.main.request.right.statusbadge;

import com.github.clagomess.tomato.dto.ResponseDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseTimeBadgeTest {
    @ParameterizedTest
    @CsvSource({
            "100,100ms",
            "20000,20s"
    })
    public void formatTime(
            Long duration,
            String expected
    ){
        var response = Mockito.mock(ResponseDto.Response.class);

        var ui = new ResponseTimeBadge(response);
        assertEquals(expected, ui.formatTime(duration));
    }
}
