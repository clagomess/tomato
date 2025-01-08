package com.github.clagomess.tomato.service.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.service.EnvironmentDataService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class HttpHeaderBuilder {
    private final EnvironmentDataService environmentDataService;

    private final HttpRequest.Builder requestBuilder;
    private final RequestDto requestDto;

    public HttpHeaderBuilder(
            HttpRequest.Builder requestBuilder,
            RequestDto requestDto
    ) {
        this(
                new EnvironmentDataService(),
                requestBuilder,
                requestDto
        );
    }

    protected String buildValue(
            List<EnvironmentDto.Env> envs,
            String value
    ){
        if(value == null) return "";

        if(envs != null){
            for(var env : envs) {
                value = value.replace(
                        String.format("{{%s}}", env.getKey()),
                        env.getValue()
                );
            }
        }

        return value;
    }

    public void build() throws IOException {
        //@TODO: get tomato version from project
        requestBuilder.setHeader("User-Agent", "Tomato/0.0.1");

        List<EnvironmentDto.Env> envs = environmentDataService.getWorkspaceSessionEnvironment()
                .map(EnvironmentDto::getEnvs)
                .orElse(Collections.emptyList());

        requestDto.getHeaders().stream()
                .filter(RequestDto.KeyValueItem::isSelected)
                .forEach(header -> requestBuilder.setHeader(
                        header.getKey(),
                        buildValue(envs, header.getValue())
                ));

        requestDto.getCookies().stream()
                .filter(RequestDto.KeyValueItem::isSelected)
                .forEach(cookie -> requestBuilder.header(
                        "Cookie",
                        cookie.getKey() + "=" + buildValue(envs, cookie.getValue())
                ));
    }
}
