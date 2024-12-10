package com.github.clagomess.tomato.dto;

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
    private LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime updateTime = LocalDateTime.now();
}
