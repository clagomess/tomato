package com.github.clagomess.tomato.factory;

import javax.swing.*;
import java.util.Objects;

public class IconFactory {
    public static final ImageIcon ICON_HTTP_METHOD_GET = new ImageIcon(Objects.requireNonNull(IconFactory.class
            .getResource("httpmethods/GET.png")));
}
