package com.github.clagomess.tomato.dto.data.keyvalue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyValueItemDto implements Comparable<KeyValueItemDto> {
    protected String key;
    protected String value;
    protected boolean selected = true;

    public KeyValueItemDto(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(KeyValueItemDto o) {
        return StringUtils.compare(this.getKey(), o.getKey());
    }
}
