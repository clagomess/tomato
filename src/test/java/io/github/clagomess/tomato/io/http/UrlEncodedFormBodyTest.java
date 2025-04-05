package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.data.request.BodyDto;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static io.github.clagomess.tomato.enums.BodyTypeEnum.URL_ENCODED_FORM;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlEncodedFormBodyTest {
    private BodyDto body;

    private final EnvironmentDto environment = new EnvironmentDto(){{
        setEnvs(List.of(new KeyValueItemDto("foo", "bar")));
    }};

    @BeforeEach
    public void setup() throws IOException {
        body = new BodyDto();
        body.setType(URL_ENCODED_FORM);
    }

    @Test
    public void build() throws IOException {
        body.setUrlEncodedForm(List.of(
                new ContentTypeKeyValueItemDto("myparam", "myvalue"),
                new ContentTypeKeyValueItemDto("utf8param", "AçãoAçucar"),
                new ContentTypeKeyValueItemDto("nullparam", null),
                new ContentTypeKeyValueItemDto("hidden", "hidden", null, false),
                new ContentTypeKeyValueItemDto(null, null),
                new ContentTypeKeyValueItemDto( " ", null)
        ));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            var urlencoded = new UrlEncodedFormBody(body);
            var result = urlencoded.build();

            assertEquals(
                    "myparam=myvalue&utf8param=A%C3%A7%C3%A3oA%C3%A7ucar&nullparam=",
                    result
            );
        }
    }

    @Test
    public void build_whenEnvDefined_replace() throws IOException {
        body.setUrlEncodedForm(List.of(
                new ContentTypeKeyValueItemDto("myparam", "{{foo}}")
        ));

        try(var ignored = Mockito.mockConstruction(
                EnvironmentRepository.class,
                (mock, context) -> Mockito.doReturn(Optional.of(environment))
                        .when(mock)
                        .getWorkspaceSessionEnvironment()
        )) {
            var urlencoded = new UrlEncodedFormBody(body);
            var result = urlencoded.build();

            assertEquals(
                    "myparam=bar",
                    result
            );
        }
    }

    @ParameterizedTest
    @CsvSource({
            "myvalue,myvalue",
            "{{foo}},bar",
            "a-{{bar}},a-%7B%7Bbar%7D%7D",
    })
    public void buildValue_assertEnvInject(String input, String expected) throws IOException {
        body.setUrlEncodedForm(List.of(
                new ContentTypeKeyValueItemDto("myparam", input)
        ));

        try(var ignored = Mockito.mockConstruction(
                EnvironmentRepository.class,
                (mock, context) -> Mockito.doReturn(Optional.of(environment))
                        .when(mock)
                        .getWorkspaceSessionEnvironment()
        )) {
            var urlencoded = new UrlEncodedFormBody(body);
            var result = urlencoded.build();

            Assertions.assertThat(result).contains(expected);
        }
    }
}
