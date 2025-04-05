package io.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.data.request.BodyDto;
import io.github.clagomess.tomato.dto.data.request.UrlParamDto;
import io.github.clagomess.tomato.enums.HttpMethodEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestDto extends MetadataDto {
    private String name = "New Request";
    private HttpMethodEnum method = HttpMethodEnum.GET;
    private String url = "http://";
    private UrlParamDto urlParam = new UrlParamDto();
    private List<KeyValueItemDto> headers = new ArrayList<>();
    private List<KeyValueItemDto> cookies = new ArrayList<>();
    private BodyDto body = new BodyDto();

    public UrlParamDto getUrlParam() {
        if(urlParam == null) urlParam = new UrlParamDto();
        return urlParam;
    }

    public BodyDto getBody() {
        if(body == null) body = new BodyDto();
        return body;
    }

    public List<KeyValueItemDto> getCookies() {
        if(cookies == null) cookies = new ArrayList<>();
        return cookies;
    }

    public List<KeyValueItemDto> getHeaders() {
        if(headers == null) headers = new ArrayList<>();
        return headers;
    }
}
