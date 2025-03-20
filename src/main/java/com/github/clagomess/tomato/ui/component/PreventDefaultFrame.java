package com.github.clagomess.tomato.ui.component;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class PreventDefaultFrame {
    public static <T extends JFrame> void toFrontIfExists(
            Class<T> frame,
            Runnable runnable
    ){
        var openedFrame = Arrays.stream(Window.getWindows())
                .filter(item -> item.getClass() == frame)
                .filter(Component::isVisible)
                .findFirst();

        if (openedFrame.isPresent()) {
            openedFrame.get().toFront();
        }else{
            runnable.run();
        }
    }

    public static <T extends JFrame> void disposeIfExists(
            Class<T> frame,
            Runnable runnable
    ){
        Arrays.stream(Window.getWindows())
                .filter(item -> item.getClass() == frame)
                .filter(Component::isVisible)
                .forEach(Window::dispose);

        runnable.run();
    }
}
