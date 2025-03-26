package com.github.clagomess.tomato.ui.component.svgicon.boxicons;

import com.github.clagomess.tomato.ui.component.svgicon.SvgIcon;

import java.awt.*;

public class BxsCircleIcon extends SvgIcon {
    public BxsCircleIcon(Color color) {
        this(color, 8);
    }

    public BxsCircleIcon(Color color, int size) {
        super(
                "bxs-circle.svg",
                size,
                size,
                color
        );
    }
}
