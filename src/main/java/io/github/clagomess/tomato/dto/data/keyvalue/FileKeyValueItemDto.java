package io.github.clagomess.tomato.dto.data.keyvalue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import static io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.FILE;
import static io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.TEXT;
import static io.github.clagomess.tomato.io.http.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static io.github.clagomess.tomato.io.http.MediaType.TEXT_PLAIN_TYPE;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileKeyValueItemDto extends ContentTypeKeyValueItemDto {
    private KeyValueTypeEnum type = TEXT;

    public FileKeyValueItemDto(String key, String value) {
        super(key, value);
    }

    public FileKeyValueItemDto(
            KeyValueTypeEnum type,
            String key,
            String value,
            String valueContentType,
            boolean selected
    ) {
        super(key, value, valueContentType, selected);
        this.type = type;
    }

    public KeyValueTypeEnum getType(){
        if(type == null) type = TEXT;
        return type;
    }

    public String getValueContentType() {
        if(type == TEXT && StringUtils.isBlank(valueContentType)){
            valueContentType = TEXT_PLAIN_TYPE;
        }

        if(type == FILE && StringUtils.isBlank(valueContentType)){
            valueContentType = APPLICATION_OCTET_STREAM_TYPE;
        }

        return valueContentType;
    }
}
