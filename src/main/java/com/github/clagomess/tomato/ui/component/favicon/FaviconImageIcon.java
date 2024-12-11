package com.github.clagomess.tomato.ui.component.favicon;

import javax.swing.*;
import java.util.Objects;

public class FaviconImageIcon extends ImageIcon {
    public FaviconImageIcon(){
        super(Objects.requireNonNull(
                FaviconImageIcon.class.getResource("favicon.png"),
                "Failed to load resource"
        ));
    }
}
