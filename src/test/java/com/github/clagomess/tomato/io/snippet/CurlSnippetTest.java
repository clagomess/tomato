package com.github.clagomess.tomato.io.snippet;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.request.BinaryBodyDto;
import com.github.clagomess.tomato.dto.data.request.RawBodyDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static com.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.FILE;
import static com.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.TEXT;
import static com.github.clagomess.tomato.io.http.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static com.github.clagomess.tomato.io.snippet.CurlSnippet.Type.BASH;

@Slf4j
public class CurlSnippetTest {
    private final CurlSnippet curlSnippet = new CurlSnippet(BASH);

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

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            var result = curlSnippet.build(request);
            Assertions.assertThat(result)
                    .isEqualToIgnoringNewLines("""
                    curl -X GET 'http://localhost:8000/echo.php?foo=bar'
                    """);
        }
    }

    @Test
    public void withCookie() throws IOException {
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setCookies(List.of(
                new KeyValueItemDto("foo", "bar")
        ));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            var result = curlSnippet.build(request);
            Assertions.assertThat(result)
                    .isEqualToIgnoringNewLines("""
                    curl -X GET 'http://localhost:8000' \\
                    -H 'Cookie: foo=bar'
                    """);
        }
    }

    @Test
    public void withHeaders() throws IOException {
        var request = new RequestDto();
        request.setUrl("http://localhost:8000");
        request.setHeaders(List.of(
                new KeyValueItemDto("foo", "bar")
        ));

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            var result = curlSnippet.build(request);
            Assertions.assertThat(result)
                    .isEqualToIgnoringNewLines("""
                    curl -X GET 'http://localhost:8000' \\
                    -H 'foo: bar'
                    """);
        }
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

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            var result = curlSnippet.build(request);
            Assertions.assertThat(result)
                    .isEqualToIgnoringNewLines("""
                    curl -X POST 'http://localhost:8000' \\
                    -d 'foo=bar'
                    """);
        }
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

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            var result = curlSnippet.build(request);
            Assertions.assertThat(result)
                    .isEqualToIgnoringNewLines("""
                    curl -X POST 'http://localhost:8000' \\
                    -F 'myfile=@/path/to/file' \\
                    -F 'foo=bar'
                    """);
        }
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

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            var result = curlSnippet.build(request);
            Assertions.assertThat(result)
                    .isEqualToIgnoringNewLines("""
                    curl -X POST 'http://localhost:8000' \\
                    -H 'Content-Type: application/json' \\
                    --data-raw '{"foo":"bar"}'
                    """);
        }
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

        try(var ignored = Mockito.mockConstruction(EnvironmentRepository.class)) {
            var result = curlSnippet.build(request);
            Assertions.assertThat(result)
                    .isEqualToIgnoringNewLines("""
                    curl -X POST 'http://localhost:8000' \\
                    -H 'Content-Type: application/octet-stream' \\
                    --data-binary '@bitmap.png'
                    """);
        }
    }
}
