package com.github.clagomess.tomato.io.http;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class RequestBuilder {
    private final EnvironmentRepository environmentRepository;
    private final RequestDto request;
    private final List<KeyValueItemDto> envs;

    public RequestBuilder(RequestDto request) throws IOException {
        this.request = request;
        this.environmentRepository = new EnvironmentRepository();
        this.envs = environmentRepository.getWorkspaceSessionEnvironment()
                .map(EnvironmentDto::getEnvs)
                .orElse(Collections.emptyList());
    }

    protected String injectEnv(
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

    public Stream<KeyValueItemDto> buildHeaders(){
        return request.getHeaders().stream()
                .filter(KeyValueItemDto::isSelected)
                .map(header -> new KeyValueItemDto(
                        header.getKey(),
                        injectEnv(envs, header.getValue())
                ));
    }

    public Stream<KeyValueItemDto> buildCookies(){
        return request.getCookies().stream()
                .filter(KeyValueItemDto::isSelected)
                .map(cookie -> new KeyValueItemDto(
                        cookie.getKey(),
                        injectEnv(envs, cookie.getValue())
                ));
    }

    public Stream<ContentTypeKeyValueItemDto> buildUrlEncodedForm(){
        return request.getBody().getUrlEncodedForm().stream()
                .filter(KeyValueItemDto::isSelected)
                .filter(item -> StringUtils.isNotBlank(item.getKey()))
                .map(item -> new ContentTypeKeyValueItemDto(
                        item.getKey(),
                        injectEnv(envs, item.getValue())
                ));
    }

    public Stream<FileKeyValueItemDto> buildMultipartFormData(){
        return request.getBody().getMultiPartForm().stream()
                .filter(FileKeyValueItemDto::isSelected)
                .filter(item -> StringUtils.isNotBlank(item.getKey()))
                .map(item -> new FileKeyValueItemDto(
                        item.getType(),
                        item.getKey(),
                        injectEnv(envs, item.getValue()),
                        item.getValueContentType(),
                        item.isSelected()
                ));
    }
}
