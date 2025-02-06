package com.github.clagomess.tomato.dto.data.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawBodyDto {
    private RawBodyTypeEnum type = RawBodyTypeEnum.TEXT;
    private String raw;
}
