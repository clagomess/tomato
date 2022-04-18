package com.github.clagomess.tomato.factory;

import com.github.clagomess.tomato.enums.HttpMethodEnum;

import javax.swing.*;
import java.util.Objects;

public class IconFactory {
    public static final ImageIcon ICON_HTTP_METHOD_DELETE = new ImageIcon(Objects.requireNonNull(IconFactory.class
            .getResource("http-methods/DELETE.png")));
    public static final ImageIcon ICON_HTTP_METHOD_GET = new ImageIcon(Objects.requireNonNull(IconFactory.class
            .getResource("http-methods/GET.png")));
    public static final ImageIcon ICON_HTTP_METHOD_POST = new ImageIcon(Objects.requireNonNull(IconFactory.class
            .getResource("http-methods/POST.png")));
    public static final ImageIcon ICON_HTTP_METHOD_PUT = new ImageIcon(Objects.requireNonNull(IconFactory.class
            .getResource("http-methods/PUT.png")));

    public static final ImageIcon ICON_FAVICON = new ImageIcon(Objects.requireNonNull(IconFactory.class
            .getResource("favicon/favicon.png")));

    public static ImageIcon createHttpMethodIcon(HttpMethodEnum httpMethod){
        switch (httpMethod){
            case DELETE:
                return ICON_HTTP_METHOD_DELETE;
            case GET:
                return ICON_HTTP_METHOD_GET;
            case POST:
                return ICON_HTTP_METHOD_POST;
            case PUT:
                return ICON_HTTP_METHOD_PUT;
        }

        return null;
    }
}
