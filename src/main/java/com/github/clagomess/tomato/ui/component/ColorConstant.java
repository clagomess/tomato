package com.github.clagomess.tomato.ui.component;

import java.awt.*;

public final class ColorConstant {
    private ColorConstant(){}

    public static final Color FOREGROUND = new Color(248, 248, 242);
    public static final Color BLUE = new Color(98, 114, 164);
    public static final Color CYAN = new Color(32, 174, 217);
    public static final Color GREEN = new Color(117, 186, 36);
    public static final Color ORANGE = new Color(255, 184, 108);
    public static final Color PINK = new Color(255, 121, 198);
    public static final Color PURPLE = new Color(189, 147, 249);
    public static final Color RED = new Color(255, 85, 85);
    public static final Color YELLOW = new Color(241, 250, 140);
    public static final Color GRAY = new Color(68, 71, 90);

    public record Match(Color background, Color foreground){}
    public static final Match ORANGE_MATCH = new Match(ORANGE, GRAY);
    public static final Match PURPLE_MATCH = new Match(PURPLE, GRAY);
    public static final Match YELLOW_MATCH = new Match(YELLOW, GRAY);
    public static final Match RED_MATCH = new Match(RED, FOREGROUND);
    public static final Match GREEN_MATCH = new Match(GREEN, FOREGROUND);
    public static final Match GRAY_MATCH = new Match(GRAY, FOREGROUND);
}
