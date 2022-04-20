package com.github.clagomess.tomato.util;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

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
}
