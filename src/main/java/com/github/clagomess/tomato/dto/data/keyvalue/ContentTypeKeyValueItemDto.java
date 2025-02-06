package com.github.clagomess.tomato.dto.data.keyvalue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import static com.github.clagomess.tomato.io.http.MediaType.TEXT_PLAIN_TYPE;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentTypeKeyValueItemDto extends KeyValueItemDto {
    protected String valueContentType = TEXT_PLAIN_TYPE;

    public ContentTypeKeyValueItemDto(String key, String value) {
        super(key, value);
    }

    public ContentTypeKeyValueItemDto(
            String key,
            String value,
            String valueContentType,
            boolean selected
    ) {
        super(key, value, selected);
        this.valueContentType = valueContentType;
    }

    public String getValueContentType() {
        if(StringUtils.isBlank(valueContentType)){
            valueContentType = TEXT_PLAIN_TYPE;
        }

        return valueContentType;
    }
}
