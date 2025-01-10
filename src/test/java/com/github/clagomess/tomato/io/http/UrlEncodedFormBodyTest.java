package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlEncodedFormBodyTest {
    @Test
    public void build() throws IOException {
        var form = List.of(
                new RequestDto.KeyValueItem(TEXT, "myparam", "myvalue", true),
                new RequestDto.KeyValueItem(TEXT, "utf8param", "AçãoAçucar", true),
                new RequestDto.KeyValueItem(TEXT, "nullparam", null, true),
                new RequestDto.KeyValueItem(TEXT, "hidden", "hidden", false),
                new RequestDto.KeyValueItem(TEXT, null, null, true),
                new RequestDto.KeyValueItem(TEXT,  " ", null, true)
        );

        var urlencoded = new UrlEncodedFormBody(form);
        var result = urlencoded.build();

        assertEquals(
                "myparam=myvalue&utf8param=A%C3%A7%C3%A3oA%C3%A7ucar&nullparam=",
                result
        );
    }

    @Test
    public void build_whenEnvDefined_replace() throws IOException {
        EnvironmentDto dto = new EnvironmentDto();
        dto.setEnvs(List.of(
                new EnvironmentDto.Env("foo", "bar")
        ));

        EnvironmentRepository environmentDSMock = Mockito.mock(
                EnvironmentRepository.class,
                Mockito.withSettings().useConstructor()
        );

        Mockito.when(environmentDSMock.getWorkspaceSessionEnvironment())
                .thenReturn(Optional.of(dto));

        var form = List.of(
                new RequestDto.KeyValueItem(TEXT, "myparam", "{{foo}}", true)
        );

        var urlencoded = new UrlEncodedFormBody(environmentDSMock, form);
        var result = urlencoded.build();

        assertEquals(
                "myparam=bar",
                result
        );
    }

    @ParameterizedTest
    @CsvSource({
            "myvalue,myvalue",
            "{{foo}},bar",
            "a-{{bar}},a-%7B%7Bbar%7D%7D",
    })
    public void buildValue_assertEnvInject(String input, String expected) {
        List<EnvironmentDto.Env> envs = List.of(
                new EnvironmentDto.Env("foo", "bar")
        );

        var form = new UrlEncodedFormBody(List.of());
        var result = form.buildValue(envs, input);

        Assertions.assertThat(result).contains(expected);
    }
}
