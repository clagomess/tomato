package com.github.clagomess.tomato.dto.data;

import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.FILE;
import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.TEXT;
import static com.github.clagomess.tomato.io.http.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static com.github.clagomess.tomato.io.http.MediaType.TEXT_PLAIN_TYPE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class KeyValueItemDto implements Comparable<KeyValueItemDto> {
    private KeyValueTypeEnum type = TEXT;
    private String key;
    private String value;
    private String valueContentType = TEXT_PLAIN_TYPE;
    private boolean selected = true;

    public KeyValueItemDto(String key, String value) {
        this.key = key;
        this.value = value;
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

    @Override
    public int compareTo(KeyValueItemDto o) {
        return StringUtils.compare(this.getKey(), o.getKey());
    }
}
