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

    @SuppressWarnings("unchecked")
    public static <T extends JFrame, E extends Throwable> void toFrontIfExists(
            Class<T> frame,
            RunnableFI<E> runnable,
            FilterFI<T> filter
    ) throws E {
        var openedFrame = Arrays.stream(Window.getWindows())
                .filter(item -> item.getClass() == frame)
                .filter(Component::isVisible)
                .filter(item -> filter.get((T) item))
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

    @FunctionalInterface
    public interface FilterFI<T> {
        boolean get(T item);
    }

    @FunctionalInterface
    public interface RunnableFI <E extends Throwable> {
        void run() throws E;
    }
}
