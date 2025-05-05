package io.github.clagomess.tomato.io.snippet;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.data.request.BinaryBodyDto;
import io.github.clagomess.tomato.dto.data.request.RawBodyDto;
import io.github.clagomess.tomato.enums.BodyTypeEnum;
import io.github.clagomess.tomato.enums.HttpMethodEnum;
import io.github.clagomess.tomato.enums.RawBodyTypeEnum;
import io.github.clagomess.tomato.io.repository.RepositoryStubs;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.FILE;
import static io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.TEXT;
import static io.github.clagomess.tomato.io.http.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static io.github.clagomess.tomato.io.snippet.CurlSnippet.Type.BASH;

@Slf4j
public class CurlSnippetTest extends RepositoryStubs {
    private final CurlSnippet curlSnippet = new CurlSnippet(BASH);

    @BeforeAll
    public static void setup(){
        System.setProperty("TOMATO_AWAYS_USE_TEST_DATA", "true");
        EnvironmentPublisher.getInstance()
                .getCurrentEnvs()
                .addListener(() -> List.of(new EnvironmentItemDto("foo", "bar")));
    }

    @AfterAll
    public static void unload(){
        System.clearProperty("TOMATO_AWAYS_USE_TEST_DATA");
        EnvironmentPublisher.getInstance()
                .getCurrentEnvs()
                .getListeners()
                .remove();
    }

    @Test
    public void queryParam() throws IOException {
        var request = new RequestDto();
        request.setUrl("http://localhost:8000/:foo");
        request.getUrlParam().setQuery(List.of(
                new ContentTypeKeyValueItemDto("foo", "bar")
        ));
        request.getUrlParam().setPath(List.of(
                new KeyValueItemDto("foo", "echo.php")
        ));

        var result = curlSnippet.build(request);
        Assertions.assertThat(result)
                .isEqualToIgnoringNewLines("""
                    curl -X GET 'http://localhost:8000/echo.php?foo=bar'
                    """);
    }

    @Test
    public void withCookie() throws IOException {
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setCookies(List.of(
                new KeyValueItemDto("foo", "bar")
        ));

        var result = curlSnippet.build(request);
        Assertions.assertThat(result)
                .isEqualToIgnoringNewLines("""
                    curl -X GET 'http://localhost:8000' \\
                    -H 'Cookie: foo=bar'
                    """);
    }

    @Test
    public void withHeaders() throws IOException {
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setHeaders(List.of(
                new KeyValueItemDto("foo", "bar")
        ));

        var result = curlSnippet.build(request);
        Assertions.assertThat(result)
                .isEqualToIgnoringNewLines("""
                    curl -X GET 'http://localhost:8000' \\
                    -H 'foo: bar'
                    """);
    }

    @Test
    public void urlEncodedForm() throws IOException {
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.URL_ENCODED_FORM);
        request.getBody().setUrlEncodedForm(List.of(
                new ContentTypeKeyValueItemDto("foo", "bar")
        ));

        var result = curlSnippet.build(request);
        Assertions.assertThat(result)
                .isEqualToIgnoringNewLines("""
                    curl -X POST 'http://localhost:8000' \\
                    -d 'foo=bar'
                    """);
    }

    @Test
    public void multiPartForm() throws IOException {
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.MULTIPART_FORM);
        request.getBody().setMultiPartForm(List.of(
                new FileKeyValueItemDto(FILE, "myfile", "/path/to/file", "application/json", true),
                new FileKeyValueItemDto(TEXT, "foo", "bar", "text/plain", true)
        ));

        var result = curlSnippet.build(request);
        Assertions.assertThat(result)
                .isEqualToIgnoringNewLines("""
                    curl -X POST 'http://localhost:8000' \\
                    -F 'myfile=@/path/to/file' \\
                    -F 'foo=bar'
                    """);
    }

    @Test
    public void raw() throws IOException {
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.RAW);
        request.getBody().setRaw(new RawBodyDto(
                RawBodyTypeEnum.JSON,
                "{\"foo\":\"bar\"}"
        ));

        var result = curlSnippet.build(request);
        Assertions.assertThat(result)
                .isEqualToIgnoringNewLines("""
                    curl -X POST 'http://localhost:8000' \\
                    -H 'Content-Type: application/json' \\
                    --data-raw '{"foo":"bar"}'
                    """);
    }

    @Test
    public void raw_whenRequestWithContentType() throws IOException {
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.setHeaders(List.of(
                new ContentTypeKeyValueItemDto("Content-Type", "text/html")
        ));
        request.getBody().setType(BodyTypeEnum.RAW);
        request.getBody().setRaw(new RawBodyDto(
                RawBodyTypeEnum.JSON,
                "{\"foo\":\"bar\"}"
        ));

        var result = curlSnippet.build(request);
        Assertions.assertThat(result)
                .isEqualToIgnoringNewLines("""
                    curl -X POST 'http://localhost:8000' \\
                    -H 'Content-Type: text/html' \\
                    --data-raw '{"foo":"bar"}'
                    """);
    }

    @Test
    public void binary() throws IOException {
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setMethod(HttpMethodEnum.POST);
        request.getBody().setType(BodyTypeEnum.BINARY);
        request.getBody().setBinary(new BinaryBodyDto(
                APPLICATION_OCTET_STREAM_TYPE,
                "bitmap.png"
        ));

        var result = curlSnippet.build(request);
        Assertions.assertThat(result)
                .isEqualToIgnoringNewLines("""
                    curl -X POST 'http://localhost:8000' \\
                    -H 'Content-Type: application/octet-stream' \\
                    --data-binary '@bitmap.png'
                    """);
    }
}
