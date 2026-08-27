package io.github.clagomess.tomato.ui.component.envtextfield;

import io.github.clagomess.tomato.ui.component.ColorConstant;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.io.IOException;
import java.util.Map;

public interface StyleMap {
    Map<String, String> getInjected();

    default void clearInjected(){
        getInjected().clear();
    }

    void update(
            StyledDocument document,
            String text
    ) throws IOException;

    static SimpleAttributeSet getRedStyle(){
        var style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, ColorConstant.RED);
        return style;
    }

    static SimpleAttributeSet getGreenStyle(){
        var style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, ColorConstant.GREEN);
        return style;
    }

    static SimpleAttributeSet getBlueStyle(){
        var style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, ColorConstant.BLUE);
        return style;
    }
}
