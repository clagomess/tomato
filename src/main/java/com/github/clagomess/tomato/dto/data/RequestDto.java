package com.github.clagomess.tomato.dto.data;

import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RequestDto extends MetadataDto {
    private String name = "New Request";
    private HttpMethodEnum method = HttpMethodEnum.GET;
    private String url;
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

    // @TODO: remove
    public MultivaluedMap<String, Object> toMultivaluedMapHeaders(){
        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        headers.forEach(item -> {
            map.put(item.getKey(), Collections.singletonList(item.getValue()));
        });

        return map;
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

        public RawBody getRaw() {
            if(raw == null) raw = new RawBody();
            return raw;
        }

        public List<KeyValueItem> getUrlEncodedForm() {
            if(urlEncodedForm == null) urlEncodedForm = new ArrayList<>();
            return urlEncodedForm;
        }

        public List<KeyValueItem> getMultiPartForm() {
            if(multiPartForm == null) multiPartForm = new ArrayList<>();
            return multiPartForm;
        }

        // @TODO: remove
        public FormDataMultiPart toMultiPartForm(){
            FormDataMultiPart form = new FormDataMultiPart();
            multiPartForm.forEach(item -> form.field(item.getKey(), item.getValue()));
            return form;
        }

        // @TODO: remove
        public MultivaluedMap<String, String> toUrlEncodedForm(){
            MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
            urlEncodedForm.forEach(item -> {
                map.put(item.getKey(), Collections.singletonList(item.getValue()));
            });

            return map;
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
        private String contentType;
        private File file;
    }

    @Data
    @NoArgsConstructor
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
