package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.io.repository.RepositoryStubs;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

class HttpHeaderBuilderTest extends RepositoryStubs {
    private HttpRequest.Builder httpRequestBuilder;

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
    void setupHttpRequest() {
        this.httpRequestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost"));
    }

    @Test
    void build_expectedDefaultUserAgent() throws IOException {
        var request = new RequestDto();

        new HttpHeaderBuilder(httpRequestBuilder, request).build();
        var result = httpRequestBuilder.build().headers();

        Assertions.assertThat(result.firstValue("User-Agent").orElseThrow())
                .contains("Tomato/");
    }

    @Test
    void build_whenOverrideUserAgent_replace() throws IOException {
        var request = new RequestDto();
        request.setHeaders(List.of(
                new KeyValueItemDto("User-Agent", "foo")
        ));

        new HttpHeaderBuilder(httpRequestBuilder, request).build();
        var result = httpRequestBuilder.build().headers();

        Assertions.assertThat(result.firstValue("User-Agent").orElseThrow())
                .contains("foo");
    }

    @Test
    void build_whenDuplicateHeader_onlyOne() throws IOException {
        var request = new RequestDto();
        request.setHeaders(List.of(
                new KeyValueItemDto("Content-Type", "foo"),
                new KeyValueItemDto("Content-Type", "bar")
        ));

        new HttpHeaderBuilder(httpRequestBuilder, request).build();
        var result = httpRequestBuilder.build().headers();

        Assertions.assertThat(result.allValues("Content-Type"))
                .hasSize(1);
    }

    @Test
    void build_whenDupliacateCookie_allowAll() throws IOException {
        var request = new RequestDto();
        request.setCookies(List.of(
                new KeyValueItemDto("JSESSIONID", "foo"),
                new KeyValueItemDto("FOO", "bar")
        ));

        new HttpHeaderBuilder(httpRequestBuilder, request).build();
        var result = httpRequestBuilder.build().headers();

        Assertions.assertThat(result.allValues("Cookie"))
                .hasSize(2);
    }

    @Test
    void build_whenEnvDefinedHeader_replace() throws IOException {
        var request = new RequestDto();
        request.setHeaders(List.of(
                new KeyValueItemDto("Content-Type", "{{foo}}")
        ));

        new HttpHeaderBuilder(httpRequestBuilder, request).build();
        var result = httpRequestBuilder.build().headers();

        Assertions.assertThat(result.firstValue("Content-Type").orElseThrow())
                .isEqualTo("bar");
    }

    @Test
    void build_whenEnvDefinedCookie_replace() throws IOException {
        var request = new RequestDto();
        request.setCookies(List.of(
                new KeyValueItemDto("JSESSIONID", "{{foo}}")
        ));

        new HttpHeaderBuilder(httpRequestBuilder, request).build();
        var result = httpRequestBuilder.build().headers();

        Assertions.assertThat(result.firstValue("Cookie").orElseThrow())
                .isEqualTo("JSESSIONID=bar");
    }
}
