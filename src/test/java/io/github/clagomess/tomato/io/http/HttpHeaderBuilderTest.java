package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

public class HttpHeaderBuilderTest {
    private HttpRequest.Builder httpRequestBuilder;

    private final EnvironmentDto environment = new EnvironmentDto(){{
        setEnvs(List.of(new EnvironmentItemDto("foo", "bar")));
    }};

    @BeforeEach
    public void setup() throws IOException {
        this.httpRequestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost"));
    }

    @Test
    public void build_expectedDefaultUserAgent() throws IOException {
        var request = new RequestDto();

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            new HttpHeaderBuilder(httpRequestBuilder, request).build();
            var result = httpRequestBuilder.build().headers();

            Assertions.assertThat(result.firstValue("User-Agent").orElseThrow())
                    .contains("Tomato/");
        }
    }

    @Test
    public void build_whenOverrideUserAgent_replace() throws IOException {
        var request = new RequestDto();
        request.setHeaders(List.of(
                new KeyValueItemDto("User-Agent", "foo")
        ));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            new HttpHeaderBuilder(httpRequestBuilder, request).build();
            var result = httpRequestBuilder.build().headers();

            Assertions.assertThat(result.firstValue("User-Agent").orElseThrow())
                    .contains("foo");
        }
    }

    @Test
    public void build_whenDuplicateHeader_onlyOne() throws IOException {
        var request = new RequestDto();
        request.setHeaders(List.of(
                new KeyValueItemDto("Content-Type", "foo"),
                new KeyValueItemDto("Content-Type", "bar")
        ));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            new HttpHeaderBuilder(httpRequestBuilder, request).build();
            var result = httpRequestBuilder.build().headers();

            Assertions.assertThat(result.allValues("Content-Type"))
                    .hasSize(1);
        }
    }

    @Test
    public void build_whenDupliacateCookie_allowAll() throws IOException {
        var request = new RequestDto();
        request.setCookies(List.of(
                new KeyValueItemDto("JSESSIONID", "foo"),
                new KeyValueItemDto("FOO", "bar")
        ));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            new HttpHeaderBuilder(httpRequestBuilder, request).build();
            var result = httpRequestBuilder.build().headers();

            Assertions.assertThat(result.allValues("Cookie"))
                    .hasSize(2);
        }
    }

    @Test
    public void build_whenEnvDefinedHeader_replace() throws IOException {
        var request = new RequestDto();
        request.setHeaders(List.of(
                new KeyValueItemDto("Content-Type", "{{foo}}")
        ));

        try(var ignored = Mockito.mockConstruction(
                EnvironmentRepository.class,
                (mock, context) -> Mockito.doReturn(Optional.of(environment))
                    .when(mock)
                    .getWorkspaceSessionEnvironment()
        )) {
            new HttpHeaderBuilder(httpRequestBuilder, request).build();
            var result = httpRequestBuilder.build().headers();

            Assertions.assertThat(result.firstValue("Content-Type").orElseThrow())
                    .isEqualTo("bar");
        }
    }

    @Test
    public void build_whenEnvDefinedCookie_replace() throws IOException {
        var request = new RequestDto();
        request.setCookies(List.of(
                new KeyValueItemDto("JSESSIONID", "{{foo}}")
        ));

        try(var ignored = Mockito.mockConstruction(
                EnvironmentRepository.class,
                (mock, context) -> Mockito.doReturn(Optional.of(environment))
                        .when(mock)
                        .getWorkspaceSessionEnvironment()
        )) {
            new HttpHeaderBuilder(httpRequestBuilder, request).build();
            var result = httpRequestBuilder.build().headers();

            Assertions.assertThat(result.firstValue("Cookie").orElseThrow())
                    .isEqualTo("JSESSIONID=bar");
        }
    }
}
