package io.github.clagomess.tomato.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileUtilsTest {
    @ParameterizedTest
    @CsvSource({
            "100,100B",
            "10240,10.00KB",
            "2048576,1.95MB",
    })
    void humanReadableByteCountBinary(
            Long size,
            String expected
    ){
        var result = FileUtils.humanReadableByteCountBinary(size);
        assertEquals(expected, result);
    }
}
