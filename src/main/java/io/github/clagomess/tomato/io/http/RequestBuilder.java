package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class RequestBuilder {
    private final List<EnvironmentItemDto> envs;

    public RequestBuilder() {
        this.envs = EnvironmentPublisher.getInstance()
                .getCurrentEnvs()
                .request();
    }

    protected String injectEnvironment(
            String value
    ){
        if(value == null) return "";
        if(envs == null || envs.isEmpty()) return value;

        StringBuilder valueBuilder = new StringBuilder(value);

        for(var env : envs) {
            int idx = 0;
            var envKey = String.format("{{%s}}", env.getKey());
            var envValue = env.getValue() != null ? env.getValue() : "";

            while ((idx = valueBuilder.indexOf(envKey, idx)) != -1){
                valueBuilder.replace(
                        idx,
                        idx + envKey.length(),
                        envValue
                );
            }
        }

        return valueBuilder.toString();
    }

    public Stream<KeyValueItemDto> buildHeaders(
            List<KeyValueItemDto> headers
    ){
        return headers.stream()
                .filter(KeyValueItemDto::isSelected)
                .map(header -> new KeyValueItemDto(
                        header.getKey(),
                        injectEnvironment(header.getValue())
                ));
    }

    public Stream<KeyValueItemDto> buildCookies(
            List<KeyValueItemDto> cookies
    ){
        return cookies.stream()
                .filter(KeyValueItemDto::isSelected)
                .map(cookie -> new KeyValueItemDto(
                        cookie.getKey(),
                        injectEnvironment(cookie.getValue())
                ));
    }

    public Stream<ContentTypeKeyValueItemDto> buildUrlEncodedForm(
            List<ContentTypeKeyValueItemDto> form
    ){
        return form.stream()
                .filter(KeyValueItemDto::isSelected)
                .filter(item -> StringUtils.isNotBlank(item.getKey()))
                .map(item -> new ContentTypeKeyValueItemDto(
                        item.getKey(),
                        injectEnvironment(item.getValue())
                ));
    }

    public Stream<FileKeyValueItemDto> buildMultipartFormData(
            List<FileKeyValueItemDto> form
    ){
        return form.stream()
                .filter(FileKeyValueItemDto::isSelected)
                .filter(item -> StringUtils.isNotBlank(item.getKey()))
                .map(item -> new FileKeyValueItemDto(
                        item.getType(),
                        item.getKey(),
                        injectEnvironment(item.getValue()),
                        item.getValueContentType(),
                        item.isSelected()
                ));
    }
}
