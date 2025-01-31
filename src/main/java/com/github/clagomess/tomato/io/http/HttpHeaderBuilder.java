package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.util.RevisionUtil;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class HttpHeaderBuilder {
    private final EnvironmentRepository environmentRepository;

    private final HttpRequest.Builder requestBuilder;
    private final RequestDto requestDto;

    public HttpHeaderBuilder(
            HttpRequest.Builder requestBuilder,
            RequestDto requestDto
    ) {
        this(
                new EnvironmentRepository(),
                requestBuilder,
                requestDto
        );
    }

    protected String buildValue(
            List<KeyValueItemDto> envs,
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
        requestBuilder.setHeader(
                "User-Agent",
                "Tomato/" + RevisionUtil.getInstance().getDeployTag()
        );

        List<KeyValueItemDto> envs = environmentRepository.getWorkspaceSessionEnvironment()
                .map(EnvironmentDto::getEnvs)
                .orElse(Collections.emptyList());

        requestDto.getHeaders().stream()
                .filter(KeyValueItemDto::isSelected)
                .forEach(header -> requestBuilder.setHeader(
                        header.getKey(),
                        buildValue(envs, header.getValue())
                ));

        requestDto.getCookies().stream()
                .filter(KeyValueItemDto::isSelected)
                .forEach(cookie -> requestBuilder.header(
                        "Cookie",
                        cookie.getKey() + "=" + buildValue(envs, cookie.getValue())
                ));
    }
}
