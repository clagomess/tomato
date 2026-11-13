package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.data.request.AuthDto;
import io.github.clagomess.tomato.dto.data.request.BasicAuthDto;
import io.github.clagomess.tomato.enums.AuthTypeEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.io.File;
import java.util.List;

import static io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.TEXT;
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

        var result = new RequestBuilder(null, envs){}
                .injectEnvironment(input);

        assertEquals(expected, result);
    }

    @Nested
    class buildHeaders {
        @ParameterizedTest
        @NullAndEmptySource
        void whenNullKey_dontAdd(String key){
            var input = List.of(new KeyValueItemDto(key, null));

            var result = new RequestBuilder(null, List.of()){}
                    .buildHeaders(input, new AuthDto());

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenNotSelected_dontAdd(){
            var input = List.of(new KeyValueItemDto("a", "a", false));

            var result = new RequestBuilder(null, List.of()){}
                    .buildHeaders(input, new AuthDto());

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenEquals_dontDuplicateObject(){
            var item = new KeyValueItemDto("a", "a");

            var result = new RequestBuilder(null, List.of()){}
                    .buildHeaders(List.of(item), new AuthDto())
                    .toList()
                    .get(0);

            assertSame(item, result);
        }

        @Test
        void whenBasicAuth_appendAuthorizationHeader(){
            var auth = new AuthDto();
            auth.setType(AuthTypeEnum.BASIC);
            auth.setBasic(new BasicAuthDto("foo", "bar"));

            var result = new RequestBuilder(null, List.of()){}
                    .buildHeaders(List.of(new KeyValueItemDto("x-foo", "bar")), auth)
                    .toList();

            Assertions.assertThat(result)
                    .extracting(KeyValueItemDto::getKey, KeyValueItemDto::getValue)
                    .containsExactly(
                            Assertions.tuple("x-foo", "bar"),
                            Assertions.tuple("Authorization", "Basic Zm9vOmJhcg==")
                    );
        }
    }

    @Nested
    class buildAuthBasic {
        @Test
        void whenNoAuth_returnEmpty(){
            var result = new RequestBuilder(null, List.of()){}
                    .buildAuthBasic(new AuthDto());

            Assertions.assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @CsvSource({
                ",bar",
                "' ',bar",
                "foo,",
                "foo,' '",
                ",",
        })
        void whenUsernameOrPasswordBlank_returnEmpty(
                String username,
                String password
        ){
            var auth = new AuthDto();
            auth.setType(AuthTypeEnum.BASIC);
            auth.setBasic(new BasicAuthDto(username, password));

            var result = new RequestBuilder(null, List.of()){}
                    .buildAuthBasic(auth);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenEnvironmentDefined_injectValues(){
            List<EnvironmentItemDto> envs = List.of(
                    new EnvironmentItemDto("user", "foo"),
                    new EnvironmentItemDto("pass", "bar")
            );

            var auth = new AuthDto();
            auth.setType(AuthTypeEnum.BASIC);
            auth.setBasic(new BasicAuthDto("{{user}}", "{{pass}}"));

            var result = new RequestBuilder(null, envs){}
                    .buildAuthBasic(auth)
                    .toList();

            Assertions.assertThat(result)
                    .singleElement()
                    .satisfies(item -> {
                        assertEquals("Authorization", item.getKey());
                        assertEquals("Basic Zm9vOmJhcg==", item.getValue());
                    });
        }

        @ParameterizedTest
        @CsvSource({
                "foo,bar,Basic Zm9vOmJhcg==",
                "çã,ü,Basic w6fDozrDvA==",
        })
        void whenBasic_returnAuthorizationHeader(
                String username,
                String password,
                String expected
        ){
            var auth = new AuthDto();
            auth.setType(AuthTypeEnum.BASIC);
            auth.setBasic(new BasicAuthDto(username, password));

            var result = new RequestBuilder(null, List.of()){}
                    .buildAuthBasic(auth)
                    .toList();

            Assertions.assertThat(result)
                    .singleElement()
                    .satisfies(item -> {
                        assertEquals("Authorization", item.getKey());
                        assertEquals(expected, item.getValue());
                    });
        }
    }

    @Nested
    class buildCookies {
        @ParameterizedTest
        @NullAndEmptySource
        void whenNullKey_dontAdd(String key){
            var input = List.of(new KeyValueItemDto(key, null));

            var result = new RequestBuilder(null, List.of()){}
                    .buildCookies(input);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenNotSelected_dontAdd(){
            var input = List.of(new KeyValueItemDto("a", "a", false));

            var result = new RequestBuilder(null, List.of()){}
                    .buildCookies(input);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenEquals_dontDuplicateObject(){
            var item = new KeyValueItemDto("a", "a");

            var result = new RequestBuilder(null, List.of()){}
                    .buildCookies(List.of(item))
                    .toList()
                    .get(0);

            assertSame(item, result);
        }
    }

    @Nested
    class buildBodyBinary {
        @ParameterizedTest
        @NullAndEmptySource
        void whenBlankFile_returnNull(String file){
            var result = new RequestBuilder(null, List.of()){}
                    .buildBodyBinary(file);

            Assertions.assertThat(result).isNull();
        }

        @Test
        void whenEnvDefined_replace(){
            List<EnvironmentItemDto> envs = List.of(
                    new EnvironmentItemDto("dir", "/tmp")
            );

            var result = new RequestBuilder(null, envs){}
                    .buildBodyBinary("{{dir}}/file.bin");

            assertEquals(new File("/tmp/file.bin"), result);
        }
    }

    @Nested
    class buildUrlEncodedForm {
        @ParameterizedTest
        @NullAndEmptySource
        void whenNullKey_dontAdd(String key){
            var input = List.of(new ContentTypeKeyValueItemDto(key, null));

            var result = new RequestBuilder(null, List.of()){}
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

            var result = new RequestBuilder(null, List.of()){}
                    .buildUrlEncodedForm(input);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenEquals_dontDuplicateObject(){
            var item = new ContentTypeKeyValueItemDto("a", "a");

            var result = new RequestBuilder(null, List.of()){}
                    .buildUrlEncodedForm(List.of(item))
                    .toList()
                    .get(0);

            assertSame(item, result);
        }
    }

    @Nested
    class buildMultipartFormData {
        @ParameterizedTest
        @NullAndEmptySource
        void whenNullKey_dontAdd(String key){
            var input = List.of(new FileKeyValueItemDto(key, null));

            var result = new RequestBuilder(null, List.of()){}
                    .buildMultipartFormData(input);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenNotSelected_dontAdd(){
            var input = List.of(new FileKeyValueItemDto(
                    TEXT,
                    "a",
                    "a",
                    "text/plain",
                    false
            ));

            var result = new RequestBuilder(null, List.of()){}
                    .buildMultipartFormData(input);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void whenEquals_dontDuplicateObject(){
            var item = new FileKeyValueItemDto("a", "a");

            var result = new RequestBuilder(null, List.of()){}
                    .buildMultipartFormData(List.of(item))
                    .toList()
                    .get(0);

            assertSame(item, result);
        }
    }
}
