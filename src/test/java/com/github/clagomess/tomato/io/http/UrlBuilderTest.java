package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UrlBuilderTest {
    private EnvironmentRepository environmentDSMock;

    private final List<EnvironmentDto.Env> envList = List.of(
            new EnvironmentDto.Env("tomatoUri", "http://localhost"),
            new EnvironmentDto.Env("foo", "bar"),
            new EnvironmentDto.Env("date", "17/01/2025"),
            new EnvironmentDto.Env("blank", " ")
    );

    private final List<RequestDto.KeyValueItem> paramList = List.of(
            new RequestDto.KeyValueItem("foo", "bar"),
            new RequestDto.KeyValueItem("date", "17/01/2025"),
            new RequestDto.KeyValueItem("date_env", "{{date}}"),
            new RequestDto.KeyValueItem("blank", " "),
            new RequestDto.KeyValueItem(TEXT, "disabled", "disabled", false)
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
        request.getUrlParam().setPath(paramList);

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
        request.getUrlParam().setQuery(paramList);

        var urlBuffer = new StringBuilder(input);

        new UrlBuilder(environmentDSMock, request)
                .buildQueryParams(urlBuffer);

        assertEquals(expected, urlBuffer.toString());
    }
}
