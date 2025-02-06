package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.request.BodyDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.github.clagomess.tomato.enums.BodyTypeEnum.URL_ENCODED_FORM;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlEncodedFormBodyTest {
    private final EnvironmentRepository environmentRepositoryMock = Mockito.mock(EnvironmentRepository.class);

    @Test
    public void build() throws IOException {
        var form = List.of(
                new ContentTypeKeyValueItemDto("myparam", "myvalue"),
                new ContentTypeKeyValueItemDto("utf8param", "AçãoAçucar"),
                new ContentTypeKeyValueItemDto("nullparam", null),
                new ContentTypeKeyValueItemDto("hidden", "hidden", null, false),
                new ContentTypeKeyValueItemDto(null, null),
                new ContentTypeKeyValueItemDto( " ", null)
        );

        var body = new BodyDto();
        body.setType(URL_ENCODED_FORM);
        body.setUrlEncodedForm(form);

        var urlencoded = new UrlEncodedFormBody(environmentRepositoryMock, body);
        var result = urlencoded.build();

        assertEquals(
                "myparam=myvalue&utf8param=A%C3%A7%C3%A3oA%C3%A7ucar&nullparam=",
                result
        );
    }

    @Test
    public void build_whenEnvDefined_replace() throws IOException {
        EnvironmentDto dto = new EnvironmentDto();
        dto.setEnvs(List.of(
                new KeyValueItemDto("foo", "bar")
        ));

        Mockito.when(environmentRepositoryMock.getWorkspaceSessionEnvironment())
                .thenReturn(Optional.of(dto));

        var form = List.of(
                new ContentTypeKeyValueItemDto("myparam", "{{foo}}")
        );
        var body = new BodyDto();
        body.setType(URL_ENCODED_FORM);
        body.setUrlEncodedForm(form);

        var urlencoded = new UrlEncodedFormBody(environmentRepositoryMock, body);
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
    public void buildValue_assertEnvInject(String input, String expected) {
        List<KeyValueItemDto> envs = List.of(
                new KeyValueItemDto("foo", "bar")
        );

        var body = new BodyDto();
        body.setType(URL_ENCODED_FORM);
        body.setUrlEncodedForm(List.of());

        var form = new UrlEncodedFormBody(environmentRepositoryMock, body);

        var result = form.buildValue(envs, input);

        Assertions.assertThat(result).contains(expected);
    }
}
