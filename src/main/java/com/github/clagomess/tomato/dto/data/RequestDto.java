package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RequestDto extends MetadataDto {
    private String name = "New Request";
    private HttpMethodEnum method = HttpMethodEnum.GET;
    private String url = "http://";
    private UrlParam urlParam = new UrlParam();
    private List<KeyValueItem> headers = new ArrayList<>();
    private List<KeyValueItem> cookies = new ArrayList<>();
    private Body body = new Body();

    public UrlParam getUrlParam() {
        if(urlParam == null) urlParam = new UrlParam();
        return urlParam;
    }

    public Body getBody() {
        if(body == null) body = new Body();
        return body;
    }

    public List<KeyValueItem> getCookies() {
        if(cookies == null) cookies = new ArrayList<>();
        return cookies;
    }

    public List<KeyValueItem> getHeaders() {
        if(headers == null) headers = new ArrayList<>();
        return headers;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class UrlParam {
        private List<KeyValueItem> path  = new ArrayList<>();
        private List<KeyValueItem> query  = new ArrayList<>();

        public List<KeyValueItem> getPath() {
            if(path == null) path = new ArrayList<>();
            return path;
        }

        public List<KeyValueItem> getQuery() {
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
        private List<KeyValueItem> urlEncodedForm  = new ArrayList<>();
        private List<KeyValueItem> multiPartForm = new ArrayList<>();

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
        public List<KeyValueItem> getUrlEncodedForm() {
            if(type != BodyTypeEnum.URL_ENCODED_FORM) return null;
            if(urlEncodedForm == null) urlEncodedForm = new ArrayList<>();
            return urlEncodedForm;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<KeyValueItem> getMultiPartForm() {
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
        private String contentType = "application/octet-stream";
        private String file;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class KeyValueItem {
        private KeyValueTypeEnum type = KeyValueTypeEnum.TEXT;
        private String key;
        private String value;
        private boolean selected = true;

        public KeyValueItem(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
