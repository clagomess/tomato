package com.github.clagomess.tomato.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.clagomess.tomato.ui.component.svgicon.http.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;

@Getter
@AllArgsConstructor
public enum HttpMethodEnum {
    POST("POST", new HttpPostIcon()),
    GET("GET", new HttpGetIcon()),
    PUT("PUT", new HttpPutIcon()),
    DELETE("DELETE", new HttpDeleteIcon()),
    HEAD("HEAD", new HttpHeadIcon()),
    OPTIONS("OPTIONS", new HttpOptionsIcon()),
    PATCH("PATCH", new HttpPatchIcon());

    private final String method;

    @JsonIgnore
    private final Icon icon;
}
