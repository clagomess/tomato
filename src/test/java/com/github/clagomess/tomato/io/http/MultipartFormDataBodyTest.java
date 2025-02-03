package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.KeyValueItemDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.FILE;
import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.TEXT;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class MultipartFormDataBodyTest {
    private final EnvironmentRepository environmentRepositoryMock = Mockito.mock(EnvironmentRepository.class);
    private final String boundary = "tomato-test";

    @Test
    public void build() throws IOException {
        String formFile = Objects.requireNonNull(getClass()
                        .getResource("MultipartFormDataBodyTest/dummy.txt"))
                .getFile();

        var form = List.of(
                new KeyValueItemDto(TEXT, "myparam", "myvalue", null, true),
                new KeyValueItemDto(TEXT, null, null, null, true),
                new KeyValueItemDto(TEXT,  " ", null, null, true),
                new KeyValueItemDto(FILE, "myfile", formFile, null, true)
        );

        var multipart = new MultipartFormDataBody(environmentRepositoryMock, boundary, form);
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
        var form = List.of(
                new KeyValueItemDto(TEXT, "myparam", null, null, true)
        );

        var multipart = new MultipartFormDataBody(environmentRepositoryMock, boundary, form);
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
        var form = List.of(
                new KeyValueItemDto(FILE, "myfile", fileParam, null, true)
        );

        var multipart = new MultipartFormDataBody(environmentRepositoryMock, boundary, form);

        assertThrows(FileNotFoundException.class, multipart::build);
    }

    @Test
    public void build_whenNotSelectedParam_notSend() throws IOException {
        var form = List.of(
                new KeyValueItemDto(TEXT, "myparam", "myvalue", null, true),
                new KeyValueItemDto(TEXT, "mysecondparam", "myvalue", null, false)
        );

        var multipart = new MultipartFormDataBody(environmentRepositoryMock, boundary, form);
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
        EnvironmentDto dto = new EnvironmentDto();
        dto.setEnvs(List.of(
                new KeyValueItemDto("foo", "bar")
        ));

        Mockito.when(environmentRepositoryMock.getWorkspaceSessionEnvironment())
                .thenReturn(Optional.of(dto));

        var form = List.of(
                new KeyValueItemDto(TEXT, "myparam", "{{foo}}", null, true)
        );

        var multipart = new MultipartFormDataBody(
                environmentRepositoryMock,
                "tomato-1",
                form
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<KeyValueItemDto> envs = List.of(
                new KeyValueItemDto("foo", "bar")
        );

        var form = new MultipartFormDataBody(List.of());
        form.writeTextBoundary(baos, envs, "mykey", input);

        Assertions.assertThat(baos.toString()).contains(expected);
    }

    @Test
    public void writeTextBoundary_whenEnvListIsNull_doNothing() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        var form = new MultipartFormDataBody(List.of());
        form.writeTextBoundary(baos, null, "mykey", "myvalue");

        Assertions.assertThat(baos.toString()).contains("myvalue");
    }
}
