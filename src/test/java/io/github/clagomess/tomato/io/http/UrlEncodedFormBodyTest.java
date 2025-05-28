package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.data.request.BodyDto;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.List;

import static io.github.clagomess.tomato.enums.BodyTypeEnum.URL_ENCODED_FORM;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UrlEncodedFormBodyTest {
    private BodyDto body;

    @BeforeAll
    static void setup(){
        EnvironmentPublisher.getInstance()
                .getCurrentEnvs()
                .addListener(() -> List.of(new EnvironmentItemDto("foo", "bar")));
    }

    @AfterAll
    static void unload(){
        EnvironmentPublisher.getInstance()
                .getCurrentEnvs()
                .getListeners()
                .remove();
    }

    @BeforeEach
    void setupBody() {
        body = new BodyDto();
        body.setType(URL_ENCODED_FORM);
    }

    @Test
    void build() throws IOException {
        body.setUrlEncodedForm(List.of(
                new ContentTypeKeyValueItemDto("myparam", "myvalue"),
                new ContentTypeKeyValueItemDto("utf8param", "AçãoAçucar"),
                new ContentTypeKeyValueItemDto("nullparam", null),
                new ContentTypeKeyValueItemDto("hidden", "hidden", null, false),
                new ContentTypeKeyValueItemDto(null, null),
                new ContentTypeKeyValueItemDto( " ", null),
                new ContentTypeKeyValueItemDto("foo[bar]", null),
                new ContentTypeKeyValueItemDto(null, null)
        ));

        var urlencoded = new UrlEncodedFormBody(body);
        var result = urlencoded.build();

        assertEquals(
                "myparam=myvalue&utf8param=A%C3%A7%C3%A3oA%C3%A7ucar&nullparam=&foo%5Bbar%5D=",
                result
        );
    }

    @Test
    void build_whenEnvDefined_replace() throws IOException {
        body.setUrlEncodedForm(List.of(
                new ContentTypeKeyValueItemDto("myparam", "{{foo}}")
        ));

        var urlencoded = new UrlEncodedFormBody(body);
        var result = urlencoded.build();

        assertEquals(
                "myparam=bar",
                result
        );
    }

    @ParameterizedTest
    @CsvSource({
            "myvalue,myvalue",
            "{{foo}},bar",
            "a-{{bar}},a-%7B%7Bbar%7D%7D",
    })
    void buildValue_assertEnvInject(String input, String expected) throws IOException {
        body.setUrlEncodedForm(List.of(
                new ContentTypeKeyValueItemDto("myparam", input)
        ));

        var urlencoded = new UrlEncodedFormBody(body);
        var result = urlencoded.build();

        Assertions.assertThat(result).contains(expected);
    }
}
