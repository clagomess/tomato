package io.github.clagomess.tomato.io.http;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor
public abstract class RequestBuilder {
    protected final RequestHeadDto requestHead;
    private final List<EnvironmentItemDto> envs;

    public RequestBuilder(
            @Nullable RequestHeadDto requestHead
    ) {
        this.requestHead = requestHead;
        this.envs = EnvironmentPublisher.getInstance()
                .getCurrentEnvs()
                .request(requestHead != null ? requestHead.getParent() : null);
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

    protected URI buildUri(RequestDto request){
        return new UrlBuilder(request).buildUri();
    }

    protected <T extends KeyValueItemDto> Stream<T> filter(Stream<T> items){
        return items
                .filter(KeyValueItemDto::isSelected)
                .filter(item -> StringUtils.isNotBlank(item.getKey()));
    }

    protected Stream<KeyValueItemDto> buildHeaders(
            List<KeyValueItemDto> headers
    ){
        return filter(headers.stream())
                .map(header -> {
                    var value = injectEnvironment(header.getValue());
                    if(Objects.equals(value, header.getValue())) return header;
                    return new KeyValueItemDto(header.getKey(), value);
                });
    }

    protected Stream<KeyValueItemDto> buildCookies(
            List<KeyValueItemDto> cookies
    ){
        return filter(cookies.stream())
                .map(cookie -> {
                    var value = injectEnvironment(cookie.getValue());
                    if(Objects.equals(value, cookie.getValue())) return cookie;
                    return new KeyValueItemDto(cookie.getKey(), value);
                });
    }

    protected File buildBodyBinary(String file){
        if(StringUtils.isBlank(file)) return null;
        var value = injectEnvironment(file);
        return new File(value);
    }

    protected Stream<ContentTypeKeyValueItemDto> buildUrlEncodedForm(
            List<ContentTypeKeyValueItemDto> form
    ){
        return filter(form.stream())
                .map(item -> {
                    var value = injectEnvironment(item.getValue());
                    if(Objects.equals(value, item.getValue())) return item;
                    return new ContentTypeKeyValueItemDto(
                            item.getKey(),
                            value,
                            item.getValueContentType(),
                            item.isSelected()
                    );
                });
    }

    protected Stream<FileKeyValueItemDto> buildMultipartFormData(
            List<FileKeyValueItemDto> form
    ){
        return filter(form.stream())
                .map(item -> {
                    var value = injectEnvironment(item.getValue());
                    if (Objects.equals(value, item.getValue())) return item;
                    return new FileKeyValueItemDto(
                            item.getType(),
                            item.getKey(),
                            injectEnvironment(item.getValue()),
                            item.getValueContentType(),
                            item.isSelected()
                    );
                });
    }
}
