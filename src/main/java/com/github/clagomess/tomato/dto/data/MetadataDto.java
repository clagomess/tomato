package com.github.clagomess.tomato.dto.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode(of = {"id"})
public abstract class MetadataDto {
    private String id = RandomStringUtils.randomAlphanumeric(8);
    private LocalDateTime createTime = LocalDateTime.now(); //@TODO: write in ISO
    private LocalDateTime updateTime = LocalDateTime.now(); //@TODO: write in ISO
}
