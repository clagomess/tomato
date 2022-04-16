package com.github.clagomess.tomato.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpMethodEnum {
    POST("POST"), GET("GET"), PUT("PUT"), DELETE("DELETE");

    private final String method;
}
