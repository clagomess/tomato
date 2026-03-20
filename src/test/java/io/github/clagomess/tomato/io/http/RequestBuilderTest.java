package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

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

    @Nested
    class buildHeaders {
        @ParameterizedTest
        @NullAndEmptySource
        void whenNullKey_dontAdd(String key){
            var input = List.of(new KeyValueItemDto(key, null));

            var result = new RequestBuilder(List.of())
                    .buildHeaders(input);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenNotSelected_dontAdd(){
            var input = List.of(new KeyValueItemDto("a", "a", false));

            var result = new RequestBuilder(List.of())
                    .buildHeaders(input);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenEquals_dontDuplicateObject(){
            var item = new KeyValueItemDto("a", "a");

            var result = new RequestBuilder(List.of())
                    .buildHeaders(List.of(item))
                    .toList()
                    .get(0);

            assertSame(item, result);
        }
    }

    @Nested
    class buildCookies {
        @ParameterizedTest
        @NullAndEmptySource
        void whenNullKey_dontAdd(String key){
            var input = List.of(new KeyValueItemDto(key, null));

            var result = new RequestBuilder(List.of())
                    .buildCookies(input);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenNotSelected_dontAdd(){
            var input = List.of(new KeyValueItemDto("a", "a", false));

            var result = new RequestBuilder(List.of())
                    .buildCookies(input);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenEquals_dontDuplicateObject(){
            var item = new KeyValueItemDto("a", "a");

            var result = new RequestBuilder(List.of())
                    .buildCookies(List.of(item))
                    .toList()
                    .get(0);

            assertSame(item, result);
        }
    }

    @Nested
    class buildUrlEncodedForm {
        @ParameterizedTest
        @NullAndEmptySource
        void whenNullKey_dontAdd(String key){
            var input = List.of(new ContentTypeKeyValueItemDto(key, null));

            var result = new RequestBuilder(List.of())
                    .buildUrlEncodedForm(input);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenNotSelected_dontAdd(){
            var input = List.of(new ContentTypeKeyValueItemDto(
                    "a",
                    "a",
                    "text/plain",
                    false
            ));

            var result = new RequestBuilder(List.of())
                    .buildUrlEncodedForm(input);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenEquals_dontDuplicateObject(){
            var item = new ContentTypeKeyValueItemDto("a", "a");

            var result = new RequestBuilder(List.of())
                    .buildUrlEncodedForm(List.of(item))
                    .toList()
                    .get(0);

            assertSame(item, result);
        }
    }
}
