package com.github.clagomess.tomato.service.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.service.EnvironmentDataService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.FILE;
import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.TEXT;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class MultipartFormDataBodyTest {
    @Test
    public void build() throws IOException {
        String formFile = getClass()
                .getResource("MultipartFormDataBodyTest/dummy.txt")
                .getFile();

        var form = List.of(
                new RequestDto.KeyValueItem(TEXT, "myparam", "myvalue", true),
                new RequestDto.KeyValueItem(TEXT, null, null, true),
                new RequestDto.KeyValueItem(TEXT,  " ", null, true),
                new RequestDto.KeyValueItem(FILE, "myfile", formFile, true)
        );

        var multipart = new MultipartFormDataBody(form);
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
                new RequestDto.KeyValueItem(TEXT, "myparam", null, true)
        );

        var multipart = new MultipartFormDataBody(form);
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
                new RequestDto.KeyValueItem(FILE, "myfile", fileParam, true)
        );

        var multipart = new MultipartFormDataBody(form);

        assertThrows(FileNotFoundException.class, multipart::build);
    }

    @Test
    public void build_whenNotSelectedParam_notSend() throws IOException {
        var form = List.of(
                new RequestDto.KeyValueItem(TEXT, "myparam", "myvalue", true),
                new RequestDto.KeyValueItem(TEXT, "mysecondparam", "myvalue", false)
        );

        var multipart = new MultipartFormDataBody(form);
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
                new EnvironmentDto.Env("foo", "bar")
        ));

        EnvironmentDataService environmentDSMock = Mockito.mock(
                EnvironmentDataService.class,
                Mockito.withSettings().useConstructor()
        );

        Mockito.when(environmentDSMock.getWorkspaceSessionEnvironment())
                .thenReturn(Optional.of(dto));

        var form = List.of(
                new RequestDto.KeyValueItem(TEXT, "myparam", "{{foo}}", true)
        );

        var multipart = new MultipartFormDataBody(
                environmentDSMock,
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
        StringWriter sw = new StringWriter();
        List<EnvironmentDto.Env> envs = List.of(
                new EnvironmentDto.Env("foo", "bar")
        );

        var form = new MultipartFormDataBody(List.of());
        form.writeTextBoundary(sw, envs, "mykey", input);

        Assertions.assertThat(sw.toString()).contains(expected);
    }

    @Test
    public void writeTextBoundary_whenEnvListIsNull_doNothing() throws IOException {
        StringWriter sw = new StringWriter();

        var form = new MultipartFormDataBody(List.of());
        form.writeTextBoundary(sw, null, "mykey", "myvalue");

        Assertions.assertThat(sw.toString()).contains("myvalue");
    }
}
