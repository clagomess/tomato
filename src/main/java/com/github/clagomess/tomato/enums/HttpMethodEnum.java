package com.github.clagomess.tomato.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.clagomess.tomato.ui.component.svgicon.http.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;

@Getter
@AllArgsConstructor
public enum HttpMethodEnum {
    POST("POST"),
    GET("GET"),
    PUT("PUT"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    PATCH("PATCH");

    private final String method;

    @JsonIgnore
    public Icon getIcon(){
        return switch (this) {
            case POST -> new HttpPostIcon();
            case GET -> new HttpGetIcon();
            case PUT -> new HttpPutIcon();
            case DELETE -> new HttpDeleteIcon();
            case HEAD -> new HttpHeadIcon();
            case OPTIONS -> new HttpOptionsIcon();
            case PATCH -> new HttpPatchIcon();
        };
    }
}
