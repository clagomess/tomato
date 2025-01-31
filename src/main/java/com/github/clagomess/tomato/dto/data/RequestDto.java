package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static com.github.clagomess.tomato.io.http.MediaType.APPLICATION_OCTET_STREAM_TYPE;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
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
    public static class UrlParam {
        private List<KeyValueItemDto> path = new ArrayList<>();
        private List<KeyValueItemDto> query = new ArrayList<>();

        public List<KeyValueItemDto> getPath() {
            if(path == null) path = new ArrayList<>();
            return path;
        }

        public List<KeyValueItemDto> getQuery() {
            if(query == null) query = new ArrayList<>();
            return query;
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Body {
        private BodyTypeEnum type = BodyTypeEnum.NO_BODY;
        private RawBody raw;
        private BinaryBody binary;
        private List<KeyValueItemDto> urlEncodedForm  = new ArrayList<>();
        private List<KeyValueItemDto> multiPartForm = new ArrayList<>();

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
        public List<KeyValueItemDto> getUrlEncodedForm() {
            if(type != BodyTypeEnum.URL_ENCODED_FORM) return null;
            if(urlEncodedForm == null) urlEncodedForm = new ArrayList<>();
            return urlEncodedForm;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<KeyValueItemDto> getMultiPartForm() {
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
    public static class RawBody {
        private RawBodyTypeEnum type = RawBodyTypeEnum.TEXT;
        private String raw;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class BinaryBody {
        private String contentType = APPLICATION_OCTET_STREAM_TYPE;
        private String file;
    }
}
