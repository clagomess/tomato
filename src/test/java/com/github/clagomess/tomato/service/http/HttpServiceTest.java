package com.github.clagomess.tomato.service.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.service.EnvironmentDataService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpServiceTest {
    @Test
    public void buildUri_whenEmptyEnv_returnsUri() throws IOException {
        EnvironmentDataService environmentDSMock = Mockito.mock(
                EnvironmentDataService.class,
                Mockito.withSettings().useConstructor()
        );

        HttpService httpService = new HttpService(
            new RequestDto(),
            new HttpDebug(),
            environmentDSMock
        );

        var result = httpService.buildUri("http://foo.bar");
        assertEquals("http://foo.bar", result.toString());
    }

    @Test
    public void buildUri_whenNotInjectedEnvAnd_throwsException() throws IOException {
        EnvironmentDataService environmentDSMock = Mockito.mock(
                EnvironmentDataService.class,
                Mockito.withSettings().useConstructor()
        );

        HttpService httpService = new HttpService(
                new RequestDto(),
                new HttpDebug(),
                environmentDSMock
        );

        assertThrows(IllegalArgumentException.class, () -> httpService.buildUri("{{foo}}"));
    }

    @Test
    public void buildUri_whenInjectedEnv_expectedReplace() throws IOException {
        EnvironmentDto dto = new EnvironmentDto();
        dto.setEnvs(List.of(
                new EnvironmentDto.Env("tomatoUri", "http://localhost"),
                new EnvironmentDto.Env("foo", "bar")
        ));

        EnvironmentDataService environmentDSMock = Mockito.mock(
                EnvironmentDataService.class,
                Mockito.withSettings().useConstructor()
        );

        Mockito.when(environmentDSMock.getWorkspaceSessionEnvironment())
                .thenReturn(Optional.of(dto));

        HttpService httpService = new HttpService(
                new RequestDto(),
                new HttpDebug(),
                environmentDSMock
        );

        var result = httpService.buildUri("{{tomatoUri}}/{{foo}}");
        assertEquals("http://localhost/bar", result.toString());
    }
}
