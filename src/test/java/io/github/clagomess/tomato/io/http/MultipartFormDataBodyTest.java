package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.request.BodyDto;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.FILE;
import static io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.TEXT;
import static io.github.clagomess.tomato.enums.BodyTypeEnum.MULTIPART_FORM;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class MultipartFormDataBodyTest {
    private final String boundary = "tomato-test";
    private BodyDto body;

    @BeforeAll
    public static void setup(){
        EnvironmentPublisher.getInstance()
                .getCurrentEnvs()
                .addListener(() -> List.of(new EnvironmentItemDto("foo", "bar")));
    }

    @AfterAll
    public static void unload(){
        EnvironmentPublisher.getInstance()
                .getCurrentEnvs()
                .getListeners()
                .remove();
    }

    @BeforeEach
    public void setupBody() {
        body = new BodyDto();
        body.setType(MULTIPART_FORM);
    }

    @Test
    public void build() throws IOException {
        String formFile = Objects.requireNonNull(getClass()
                        .getResource("MultipartFormDataBodyTest/dummy.txt"))
                .getFile();

        body.setMultiPartForm(List.of(
                new FileKeyValueItemDto(TEXT, "myparam", "myvalue", null, true),
                new FileKeyValueItemDto(TEXT, null, null, null, true),
                new FileKeyValueItemDto(TEXT,  " ", null, null, true),
                new FileKeyValueItemDto(FILE, "myfile", formFile, null, true)
        ));

        var multipart = new MultipartFormDataBody(boundary, body);
        var tmpFile = multipart.build();

        Assertions.assertThat(tmpFile)
                .isFile()
                .content()
                .contains("--tomato-")
                .contains(
                        """
                        Content-Type: text/plain\r
                        Content-Disposition: form-data; name="myparam"\r
                        \r
                        myvalue"""
                )
                .contains(
                        """
                        Content-Type: application/octet-stream\r
                        Content-Disposition: form-data; name="myfile"; filename="dummy.txt"\r
                        \r
                        Hello"""
                )
        ;
    }

    @Test
    public void build_whenNullTextParam_sendEmpty() throws IOException {
        body.setMultiPartForm(List.of(
                new FileKeyValueItemDto(TEXT, "myparam", null, null, true)
        ));

        var multipart = new MultipartFormDataBody(boundary, body);
        var tmpFile = multipart.build();

        Assertions.assertThat(tmpFile)
                .isFile()
                .content()
                .contains("Content-Disposition: form-data; name=\"myparam\"")
        ;
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"foo/bar"})
    public void build_whenNullOrInvalidFileParam_throws(String fileParam) {
        body.setMultiPartForm(List.of(
                new FileKeyValueItemDto(FILE, "myfile", fileParam, null, true)
        ));

        var multipart = new MultipartFormDataBody(boundary, body);

        assertThrows(FileNotFoundException.class, multipart::build);
    }

    @Test
    public void build_whenNotSelectedParam_notSend() throws IOException {
        body.setMultiPartForm(List.of(
                new FileKeyValueItemDto(TEXT, "myparam", "myvalue", null, true),
                new FileKeyValueItemDto(TEXT, "mysecondparam", "myvalue", null, false)
        ));

        var multipart = new MultipartFormDataBody(boundary, body);
        var tmpFile = multipart.build();

        Assertions.assertThat(tmpFile)
                .isFile()
                .content()
                .contains("Content-Disposition: form-data; name=\"myparam\"")
                .doesNotContain("Content-Disposition: form-data; name=\"mysecondparam\"")
        ;
    }

    @Test
    public void build_whenEnvDefined_replace() throws IOException {
        body.setMultiPartForm(List.of(
                new FileKeyValueItemDto(TEXT, "myparam", "{{foo}}", null, true)
        ));

        var multipart = new MultipartFormDataBody(
                "tomato-1",
                body
        );
        var tmpFile = multipart.build();

        Assertions.assertThat(tmpFile)
                .isFile()
                .content()
                .contains("bar")
        ;
    }

    @ParameterizedTest
    @CsvSource({
            "myvalue,myvalue",
            "{{foo}},bar",
            "a-{{bar}},a-{{bar}}",
    })
    public void writeTextBoundary_assertEnvInject(String input, String expected) throws IOException {
        body.setMultiPartForm(List.of(
                new FileKeyValueItemDto("mykey", input)
        ));

        var form = new MultipartFormDataBody(body);
        var tmpFile = form.build();

        Assertions.assertThat(tmpFile)
                .isFile()
                .content()
                .contains(expected)
        ;
    }
}
