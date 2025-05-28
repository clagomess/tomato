package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestBuilderTest {
    @ParameterizedTest
    @CsvSource({
            "myvalue,myvalue",
            "{{foo}},bar",
            "a-{{bar}},a-{{bar}}",
            "a-{{null-env}},a-",
    })
    void injectEnvironment(String input, String expected){
        List<EnvironmentItemDto> envs = List.of(
                new EnvironmentItemDto("foo", "bar"),
                new EnvironmentItemDto("null-env", null)
        );

        var result = new RequestBuilder(envs)
                .injectEnvironment(input);

        assertEquals(expected, result);
    }
}
