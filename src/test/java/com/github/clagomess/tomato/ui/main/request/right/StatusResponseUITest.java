package com.github.clagomess.tomato.ui.main.request.right;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class StatusResponseUITest {
    @ParameterizedTest
    @CsvSource({
            "100,100ms",
            "20000,20s"
    })
    public void formatResponseTime(
            Long duration,
            String expected
    ){
        var ui = new StatusResponseUI();
        assertEquals(expected, ui.formatResponseTime(duration));
    }

    @ParameterizedTest
    @CsvSource({
            "100,100B",
            "10240,10.00KB",
            "2048576,1.95MB",
    })
    public void formatResponseBodySize(
            Long size,
            String expected
    ){
        var ui = new StatusResponseUI();
        assertEquals(expected, ui.formatResponseBodySize(size));
    }
}
