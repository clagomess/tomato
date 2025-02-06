package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.FileValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import lombok.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.github.clagomess.tomato.io.http.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestDto extends MetadataDto {
    private String name = "New Request";
    private HttpMethodEnum method = HttpMethodEnum.GET;
    private String url = "http://";
    private UrlParam urlParam = new UrlParam();
    private List<KeyValueItemDto> headers = new ArrayList<>();
    private List<KeyValueItemDto> cookies = new ArrayList<>();
    private Body body = new Body();

    public UrlParam getUrlParam() {
        if(urlParam == null) urlParam = new UrlParam();
        return urlParam;
    }

    public Body getBody() {
        if(body == null) body = new Body();
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

    @Getter
    @Setter
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UrlParam {
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

    @Getter
    @Setter
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private BodyTypeEnum type = BodyTypeEnum.NO_BODY;
        private Charset charset;
        private RawBody raw;
        private BinaryBody binary;
        private List<ContentTypeKeyValueItemDto> urlEncodedForm  = new ArrayList<>();
        private List<FileValueItemDto> multiPartForm = new ArrayList<>();

        public Charset getCharset() {
            if(charset == null) charset = UTF_8;
            return charset;
        }

        public BodyTypeEnum getType() {
            if(type == null) type = BodyTypeEnum.NO_BODY;
            return type;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public RawBody getRaw() {
            if(type != BodyTypeEnum.RAW) return null;
            if(raw == null) raw = new RawBody();
            return raw;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public BinaryBody getBinary() {
            if(type != BodyTypeEnum.BINARY) return null;
            if(binary == null) binary = new BinaryBody();
            return binary;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<ContentTypeKeyValueItemDto> getUrlEncodedForm() {
            if(type != BodyTypeEnum.URL_ENCODED_FORM) return null;
            if(urlEncodedForm == null) urlEncodedForm = new ArrayList<>();
            return urlEncodedForm;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<FileValueItemDto> getMultiPartForm() {
            if(type != BodyTypeEnum.MULTIPART_FORM) return null;
            if(multiPartForm == null) multiPartForm = new ArrayList<>();
            return multiPartForm;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RawBody {
        private RawBodyTypeEnum type = RawBodyTypeEnum.TEXT;
        private String raw;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BinaryBody {
        private String contentType = APPLICATION_OCTET_STREAM_TYPE;
        private String file;
    }
}
