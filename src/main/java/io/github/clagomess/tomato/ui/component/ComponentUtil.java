package io.github.clagomess.tomato.ui.component;

import io.github.clagomess.tomato.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

import static javax.swing.SwingUtilities.isEventDispatchThread;

public class ComponentUtil {
    public static int getComponentIndex(Container container, JComponent searched){
        container.getComponentCount();

        for(int i = 0; i < container.getComponentCount(); i++){
            if(Objects.equals(container.getComponents()[i], searched)){
                return i;
            }
        }

        return -1;
    }

    public static void checkIsEventDispatchThread(){
        if(!isEventDispatchThread()){
            throw new IllegalThreadStateException(
                    "Not running on EDT. Current thread is: " +
                    Thread.currentThread().getName()
            );
        }
    }

    public static Window getMainWindow(){
        return Arrays.stream(Window.getWindows())
                .filter(item -> item instanceof MainFrame)
                .findFirst()
                .orElse(null);
    }
}
