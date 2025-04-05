package com.github.clagomess.tomato.dto.data.keyvalue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

import static com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum.TEXT;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentItemDto implements Comparable<EnvironmentItemDto> {
    private EnvironmentItemTypeEnum type;
    private UUID secretId;
    private String key;
    private String value;

    public EnvironmentItemDto(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public EnvironmentItemTypeEnum getType() {
        if(type == null) type = TEXT;
        return type;
    }

    @Override
    public int compareTo(EnvironmentItemDto o) {
        return StringUtils.compareIgnoreCase(this.getKey(), o.getKey());
    }
}
