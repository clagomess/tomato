package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.github.clagomess.tomato.io.http.MediaType.TEXT_PLAIN_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UrlBuilderTest {
    private EnvironmentRepository environmentDSMock;

    private final List<EnvironmentItemDto> envList = List.of(
            new EnvironmentItemDto("tomatoUri", "http://localhost"),
            new EnvironmentItemDto("foo", "bar"),
            new EnvironmentItemDto("date", "17/01/2025"),
            new EnvironmentItemDto("blank", " ")
    );

    @BeforeEach
    public void setup() throws IOException {
        environmentDSMock = Mockito.mock(
                EnvironmentRepository.class,
                Mockito.withSettings().useConstructor()
        );

        EnvironmentDto dto = new EnvironmentDto();
        dto.setEnvs(envList);

        Mockito.when(environmentDSMock.getWorkspaceSessionEnvironment())
                .thenReturn(Optional.of(dto));
    }

    @Test
    public void buildUri_whenEmptyEnv_returnsUri() throws IOException {
        var requestDto = new RequestDto();
        requestDto.setUrl("http://foo.bar");

        var urlBuilder = new UrlBuilder(
                environmentDSMock,
                requestDto
        );

        var result = urlBuilder.buildUri();
        assertEquals("http://foo.bar", result.toString());
    }

    @Test
    public void buildUri_whenNotInjectedEnvAnd_throwsException() throws IOException {
        var requestDto = new RequestDto();
        requestDto.setUrl("{{xyz}}");

        UrlBuilder urlBuilder = new UrlBuilder(
                environmentDSMock,
                requestDto
        );

        assertThrows(IllegalArgumentException.class, urlBuilder::buildUri);
    }

    @Test
    public void buildUri_whenInjectedEnv_expectedReplace() throws IOException {
        var requestDto = new RequestDto();
        requestDto.setUrl("{{tomatoUri}}/{{foo}}");

        UrlBuilder urlBuilder = new UrlBuilder(
                environmentDSMock,
                requestDto
        );

        var result = urlBuilder.buildUri();
        assertEquals("http://localhost/bar", result.toString());
    }

    @ParameterizedTest
    @CsvSource({
            "{{tomatoUri}}/hello,http://localhost/hello",
            "{{tomatoUri}}/{{foo}},http://localhost/bar",
            "{{tomatoUri}}/{{foo}}/{{foo}},http://localhost/bar/bar",
    })
    public void buildUrlEnvironment(
            String input,
            String expected
    ) throws IOException {
        var urlBuffer = new StringBuilder(input);

        new UrlBuilder(environmentDSMock, new RequestDto())
                .buildUrlEnvironment(urlBuffer);

        assertEquals(expected, urlBuffer.toString());
    }

    @ParameterizedTest
    @CsvSource({
            "hello,hello",
            "{{blank}},+",
            "{{date}},17%2F01%2F2025",
            "17/01/2025,17%2F01%2F2025",
            "{{foo}}/{{foo}},bar%2Fbar",
    })
    public void buildEncodedParamValue(
            String input,
            String expected
    ) throws IOException {
        var result = new UrlBuilder(environmentDSMock, new RequestDto())
                .buildEncodedParamValue(input);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            ":foo,bar",
            ":date,17%2F01%2F2025",
            ":date_env,17%2F01%2F2025",
            ":foo/:foo,bar/bar",
            ":disabled,:disabled",
            ":notInjected,:notInjected",
    })
    public void buildPathVariables(
            String input,
            String expected
    ) throws IOException {
        var request =  new RequestDto();
        request.getUrlParam().setPath(List.of(
                new KeyValueItemDto("foo", "bar"),
                new KeyValueItemDto("date", "17/01/2025"),
                new KeyValueItemDto("date_env", "{{date}}"),
                new KeyValueItemDto("blank", " "),
                new KeyValueItemDto("disabled", "disabled", false)
        ));

        var urlBuffer = new StringBuilder(input);

        new UrlBuilder(environmentDSMock, request)
                .buildPathVariables(urlBuffer);

        assertEquals(expected, urlBuffer.toString());
    }

    @ParameterizedTest
    @CsvSource({
            "localhost,localhost?blank=+&date=17%2F01%2F2025&date_env=17%2F01%2F2025&foo=bar",
            "localhost?,localhost?blank=+&date=17%2F01%2F2025&date_env=17%2F01%2F2025&foo=bar",
            "localhost?xpto=aaa,localhost?xpto=aaa&blank=+&date=17%2F01%2F2025&date_env=17%2F01%2F2025&foo=bar",
            "localhost?xpto=aaa&ex=a,localhost?xpto=aaa&ex=a&blank=+&date=17%2F01%2F2025&date_env=17%2F01%2F2025&foo=bar",
    })
    public void buildQueryParams(
            String input,
            String expected
    ) throws IOException {
        var request =  new RequestDto();
        request.getUrlParam().setQuery(List.of(
                new ContentTypeKeyValueItemDto("foo", "bar"),
                new ContentTypeKeyValueItemDto("date", "17/01/2025"),
                new ContentTypeKeyValueItemDto("date_env", "{{date}}"),
                new ContentTypeKeyValueItemDto("blank", " "),
                new ContentTypeKeyValueItemDto("disabled", "disabled", TEXT_PLAIN_TYPE, false)
        ));

        var urlBuffer = new StringBuilder(input);

        new UrlBuilder(environmentDSMock, request)
                .buildQueryParams(urlBuffer);

        assertEquals(expected, urlBuffer.toString());
    }
}
