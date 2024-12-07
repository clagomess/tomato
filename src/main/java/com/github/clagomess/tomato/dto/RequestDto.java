package com.github.clagomess.tomato.dto;

import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

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

    public MultivaluedMap<String, Object> toMultivaluedMapHeaders(){
        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        headers.forEach(item -> {
            map.put(item.getKey(), Collections.singletonList(item.getValue()));
        });

        return map;
    }

    @Data
    public static class Body {
        private BodyTypeEnum bodyType = BodyTypeEnum.NO_BODY;
        private String bodyContentType;
        private String raw;
        private String binaryFilePath;
        private List<KeyValueItem> urlEncodedForm;
        private List<MultiPartFormItem> multiPartForm = new ArrayList<>();

        public FormDataMultiPart toMultiPartForm(){
            FormDataMultiPart form = new FormDataMultiPart();
            multiPartForm.forEach(item -> form.field(item.getKey(), item.getValue()));
            return form;
        }

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
    @EqualsAndHashCode(callSuper = true)
    public static class KeyValueItem extends MetadataDto {
        private boolean selected = true;
        private String key;
        private String value;

        public KeyValueItem(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class MultiPartFormItem extends KeyValueItem{
        private KeyValueTypeEnum type = KeyValueTypeEnum.TEXT;

        public MultiPartFormItem(String key, String value) {
            super(key, value);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
