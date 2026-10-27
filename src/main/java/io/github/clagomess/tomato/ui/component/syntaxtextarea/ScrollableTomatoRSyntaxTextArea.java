package io.github.clagomess.tomato.ui.component.syntaxtextarea;

import io.github.clagomess.tomato.ui.component.ColorConstant;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.border.AbstractBorder;
import javax.swing.border.MatteBorder;

public class ScrollableTomatoRSyntaxTextArea extends RTextScrollPane {
    public ScrollableTomatoRSyntaxTextArea(
            TomatoRSyntaxTextArea textarea,
            AbstractBorder border
    ) {
        super(textarea);
        setBorder(border);
        getGutter().setBorderColor(ColorConstant.GRAY);
    }

    public ScrollableTomatoRSyntaxTextArea(TomatoRSyntaxTextArea textarea) {
        this(textarea, new MatteBorder(1, 1, 1, 1, ColorConstant.GRAY));
    }
}
