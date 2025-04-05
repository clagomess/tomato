package io.github.clagomess.tomato.dto.data.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlParamDto {
    private Charset charset;
    private List<KeyValueItemDto> path = new ArrayList<>();
    private List<ContentTypeKeyValueItemDto> query = new ArrayList<>();

    public Charset getCharset() {
        if(charset == null) charset = UTF_8;
        return charset;
    }

    public List<KeyValueItemDto> getPath() {
        if(path == null) path = new ArrayList<>();
        return path;
    }

    public List<ContentTypeKeyValueItemDto> getQuery() {
        if(query == null) query = new ArrayList<>();
        return query;
    }
}
