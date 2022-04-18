package com.github.clagomess.tomato.dto;

import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

@Data
public class RequestDto extends TomatoMetadataDto {
    private String name;
    private HttpMethodEnum method = HttpMethodEnum.GET;
    private String url;
    private List<KeyValueItem> headers = new ArrayList<>();
    private Body body = new Body();

    public RequestDto(String name) {
        this.name = name;
    }

    @Data
    public static class Body {
        private BodyTypeEnum bodyType = BodyTypeEnum.NO_BODY;
        private String raw;
        private String binaryFilePath;
        private List<KeyValueItem> urlEncodedForm;
        private List<MultiPartFormItem> multiPartForm;

        public FormDataMultiPart toMultiPartForm(){
            return null; //@TODO: implements parse to FormDataMultiPart
        }

        public MultivaluedMap<String, String> toUrlEncodedForm(){
            return null; //@TODO: implements parser to MultivaluedMap
        }
    }

    @Data
    @NoArgsConstructor
    public static class KeyValueItem extends TomatoMetadataDto {
        private boolean selected = true;
        private String key;
        private String value;

        public KeyValueItem(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    @Data
    private static class MultiPartFormItem extends KeyValueItem{
        private KeyValueTypeEnum type = KeyValueTypeEnum.TEXT;
    }

    @Override
    public String toString() {
        return name;
    }
}
