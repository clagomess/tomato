package com.github.clagomess.tomato.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public abstract class TomatoMetadataDto {
    private String id = UUID.randomUUID().toString();
    // private LocalDateTime createTime = LocalDateTime.now(); //@TODO: needs to implements
    // private LocalDateTime updateTime = LocalDateTime.now(); //@TODO: needs to implements
}
