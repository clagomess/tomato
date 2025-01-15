package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UrlBuilderTest {
    private EnvironmentRepository environmentDSMock;

    @BeforeEach
    public void setup(){
        environmentDSMock = Mockito.mock(
                EnvironmentRepository.class,
                Mockito.withSettings().useConstructor()
        );
    }

    @Test
    public void buildUri_whenEmptyEnv_returnsUri() throws IOException {
        UrlBuilder urlBuilder = new UrlBuilder(
                environmentDSMock,
                "http://foo.bar"
        );

        var result = urlBuilder.buildUri();
        assertEquals("http://foo.bar", result.toString());
    }

    @Test
    public void buildUri_whenNotInjectedEnvAnd_throwsException() {
        UrlBuilder urlBuilder = new UrlBuilder(
                environmentDSMock,
                "{{foo}}"
        );

        assertThrows(IllegalArgumentException.class, urlBuilder::buildUri);
    }

    @Test
    public void buildUri_whenInjectedEnv_expectedReplace() throws IOException {
        EnvironmentDto dto = new EnvironmentDto();
        dto.setEnvs(List.of(
                new EnvironmentDto.Env("tomatoUri", "http://localhost"),
                new EnvironmentDto.Env("foo", "bar")
        ));

        Mockito.when(environmentDSMock.getWorkspaceSessionEnvironment())
                .thenReturn(Optional.of(dto));

        UrlBuilder urlBuilder = new UrlBuilder(
                environmentDSMock,
                "{{tomatoUri}}/{{foo}}"
        );

        var result = urlBuilder.buildUri();
        assertEquals("http://localhost/bar", result.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://localhost?",
            "http://localhost"
    })
    public void updateQueryParam_whenEmpty_returnsEmpty(String url) {
        UrlBuilder urlBuilder = new UrlBuilder(
                environmentDSMock,
                url
        );

        var result = new ArrayList<RequestDto.KeyValueItem>();
        urlBuilder.updateQueryParam(result);
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void updateQueryParam_whenEmpty_disableNotMached() {
        UrlBuilder urlBuilder = new UrlBuilder(
                environmentDSMock,
                "http://localhost?"
        );

        var result = new ArrayList<RequestDto.KeyValueItem>();
        result.add(new RequestDto.KeyValueItem("key", "value"));

        urlBuilder.updateQueryParam(result);

        Assertions.assertThat(result).isNotEmpty();
        assertFalse(result.get(0).isSelected());
    }

    @Test
    public void updateQueryParam_whenMatch_enable() {
        UrlBuilder urlBuilder = new UrlBuilder(
                environmentDSMock,
                "http://localhost?key=value"
        );

        var result = new ArrayList<RequestDto.KeyValueItem>();
        result.add(new RequestDto.KeyValueItem("key", "value"));

        urlBuilder.updateQueryParam(result);

        Assertions.assertThat(result).isNotEmpty();
        assertTrue(result.get(0).isSelected());
    }

    @Test
    public void updateQueryParam_whenHasValues_update() {
        UrlBuilder urlBuilder = new UrlBuilder(
                environmentDSMock,
                "http://localhost?foo=bar&date=01%2F03%2F2021&a="
        );

        var result = new ArrayList<RequestDto.KeyValueItem>();

        urlBuilder.updateQueryParam(result);

        Assertions.assertThat(result)
                .contains(new RequestDto.KeyValueItem("foo", "bar"))
                .contains(new RequestDto.KeyValueItem("date", "01%2F03%2F2021"));
    }

    @Test
    public void updateQueryParam_whenInjectedEnvAnd() throws IOException {
        UrlBuilder urlBuilder = new UrlBuilder(
                environmentDSMock,
                "{{tomatoUri}}?foo={{bar}}"
        );

        EnvironmentDto dto = new EnvironmentDto();
        dto.setEnvs(List.of(
                new EnvironmentDto.Env("tomatoUri", "http://localhost"),
                new EnvironmentDto.Env("bar", "bar")
        ));

        Mockito.when(environmentDSMock.getWorkspaceSessionEnvironment())
                .thenReturn(Optional.of(dto));

        var result = new ArrayList<RequestDto.KeyValueItem>();

        urlBuilder.updateQueryParam(result);

        Assertions.assertThat(result)
                .contains(new RequestDto.KeyValueItem("foo", "{{bar}}"));
    }

    @ParameterizedTest
    @CsvSource({
            "http://localhost,http://localhost?foo=bar",
            "http://localhost,http://localhost?",
            "http://localhost,http://localhost",
            "{{tomatoUri}}/:opa,{{tomatoUri}}/:opa",
    })
    public void recreateUrl_whenEmptyQuery(
            String expected,
            String input
    ) {
        UrlBuilder urlBuilder = new UrlBuilder(environmentDSMock, input);

        var result = urlBuilder.recreateUrl(List.of());
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            "http://localhost?foo=bar,foo,bar",
            "http://localhost?foo={{bar}},foo,{{bar}}",
            "http://localhost?foo=01%2F01,foo,01/01",
            "http://localhost,,",
            "http://localhost,,aaa",
    })
    public void recreateUrl_whenNotEmptyQuery(
            String expected,
            String key,
            String value
    ){
        UrlBuilder urlBuilder = new UrlBuilder(
                environmentDSMock,
                "http://localhost"
        );

        var param = new RequestDto.KeyValueItem(key, value);

        var result = urlBuilder.recreateUrl(List.of(param));
        assertEquals(expected, result);
    }
}
