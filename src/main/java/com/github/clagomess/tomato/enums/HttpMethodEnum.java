package com.github.clagomess.tomato.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.clagomess.tomato.ui.component.svgicon.http.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;

@Getter
@AllArgsConstructor
public enum HttpMethodEnum {
    POST(new HttpPostIcon()),
    GET(new HttpGetIcon()),
    PUT(new HttpPutIcon()),
    DELETE(new HttpDeleteIcon()),
    HEAD(new HttpHeadIcon()),
    OPTIONS(new HttpOptionsIcon()),
    PATCH(new HttpPatchIcon());

    @JsonIgnore
    private final Icon icon;
}
