package com.github.clagomess.tomato.ui.main.request.right.statusbadge;

import com.github.clagomess.tomato.dto.ResponseDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseSizeBadgeTest {
    @ParameterizedTest
    @CsvSource({
            "100,100B",
            "10240,10.00KB",
            "2048576,1.95MB",
    })
    public void formatSize(
            Integer size,
            String expected
    ){
        var response = new ResponseDto.Response();
        response.setBodySize(size);

        var ui = new ResponseSizeBadge(response);
        assertEquals(expected, ui.formatSize(size));
    }
}
