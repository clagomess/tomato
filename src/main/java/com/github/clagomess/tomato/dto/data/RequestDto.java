package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RequestDto extends MetadataDto {
    private String name = "New Request";
    private HttpMethodEnum method = HttpMethodEnum.GET;
    private String url = "http://";
    private List<KeyValueItem> headers = new ArrayList<>();
    private List<KeyValueItem> cookies = new ArrayList<>();
    private Body body = new Body();

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

    @Data
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RawBody {
        private RawBodyTypeEnum type = RawBodyTypeEnum.TEXT;
        private String raw;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BinaryBody {
        private String contentType = "application/octet-stream";
        private String file;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
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
