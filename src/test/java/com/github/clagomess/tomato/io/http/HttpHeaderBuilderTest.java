package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpHeaderBuilderTest {
    private final EnvironmentRepository environmentRepositoryMock = Mockito.mock(EnvironmentRepository.class);

    @Test
    public void build_expectedDefaultUserAgent() throws IOException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost"));

        var request = new RequestDto();

        new HttpHeaderBuilder(environmentRepositoryMock, requestBuilder, request).build();
        var result = requestBuilder.build().headers();

        Assertions.assertThat(result.firstValue("User-Agent").orElseThrow())
                .contains("Tomato/");
    }

    @Test
    public void build_whenOverrideUserAgent_replace() throws IOException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost"));

        var request = new RequestDto();
        request.setHeaders(List.of(
                new KeyValueItemDto("User-Agent", "foo")
        ));

        new HttpHeaderBuilder(environmentRepositoryMock, requestBuilder, request).build();
        var result = requestBuilder.build().headers();

        Assertions.assertThat(result.firstValue("User-Agent").orElseThrow())
                .contains("foo");
    }

    @Test
    public void build_whenDuplicateHeader_onlyOne() throws IOException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost"));

        var request = new RequestDto();
        request.setHeaders(List.of(
                new KeyValueItemDto("Content-Type", "foo"),
                new KeyValueItemDto("Content-Type", "bar")
        ));

        new HttpHeaderBuilder(environmentRepositoryMock, requestBuilder, request).build();
        var result = requestBuilder.build().headers();

        Assertions.assertThat(result.allValues("Content-Type"))
                .hasSize(1);
    }

    @Test
    public void build_whenDupliacateCookie_allowAll() throws IOException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost"));

        var request = new RequestDto();
        request.setCookies(List.of(
                new KeyValueItemDto("JSESSIONID", "foo"),
                new KeyValueItemDto("FOO", "bar")
        ));

        new HttpHeaderBuilder(environmentRepositoryMock, requestBuilder, request).build();
        var result = requestBuilder.build().headers();

        Assertions.assertThat(result.allValues("Cookie"))
                .hasSize(2);
    }

    @Test
    public void build_whenEnvDefinedHeader_replace() throws IOException {
        EnvironmentDto dto = new EnvironmentDto();
        dto.setEnvs(List.of(
                new KeyValueItemDto("foo", "bar")
        ));

        Mockito.when(environmentRepositoryMock.getWorkspaceSessionEnvironment())
                .thenReturn(Optional.of(dto));

        // ---
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost"));

        var request = new RequestDto();
        request.setHeaders(List.of(
                new KeyValueItemDto("Content-Type", "{{foo}}")
        ));

        new HttpHeaderBuilder(environmentRepositoryMock, requestBuilder, request).build();
        var result = requestBuilder.build().headers();

        Assertions.assertThat(result.firstValue("Content-Type").orElseThrow())
                .isEqualTo("bar");
    }

    @Test
    public void build_whenEnvDefinedCookie_replace() throws IOException {
        EnvironmentDto dto = new EnvironmentDto();
        dto.setEnvs(List.of(
                new KeyValueItemDto("foo", "bar")
        ));

        Mockito.when(environmentRepositoryMock.getWorkspaceSessionEnvironment())
                .thenReturn(Optional.of(dto));

        // ---
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost"));

        var request = new RequestDto();
        request.setCookies(List.of(
                new KeyValueItemDto("JSESSIONID", "{{foo}}")
        ));

        new HttpHeaderBuilder(environmentRepositoryMock, requestBuilder, request).build();
        var result = requestBuilder.build().headers();

        Assertions.assertThat(result.firstValue("Cookie").orElseThrow())
                .isEqualTo("JSESSIONID=bar");
    }

    @ParameterizedTest
    @CsvSource({
            "myvalue,myvalue",
            "{{foo}},bar",
            "a-{{bar}},a-{{bar}}",
    })
    public void buildValue_assertEnvInject(String input, String expected){
        List<KeyValueItemDto> envs = List.of(
                new KeyValueItemDto("foo", "bar")
        );

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost"));
        var request = new RequestDto();

        var result = new HttpHeaderBuilder(environmentRepositoryMock, requestBuilder, request)
                .buildValue(envs, input);

        assertEquals(expected, result);
    }
}
