package com.github.clagomess.tomato.dto;

import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class RequestDto {
    private String id = UUID.randomUUID().toString();
    private String name;
    private HttpMethodEnum method = HttpMethodEnum.GET;
    private String url;
    private List<KeyValueItem> headers = new ArrayList<>();
    private Body body = new Body();

    public RequestDto(String name) {
        this.name = name;
    }

    @Data
    private static class Body {
        private BodyTypeEnum bodyType = BodyTypeEnum.NO_BODY;
        private String raw;
        private String binaryFilePath;
        private List<KeyValueItem> urlEncodedForm;
        private List<MultiPartFormItem> multiPartForm;
    }

    @Data
    private static class KeyValueItem {
        private String id = UUID.randomUUID().toString();
        private boolean selected = true;
        private String key;
        private String value;
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
