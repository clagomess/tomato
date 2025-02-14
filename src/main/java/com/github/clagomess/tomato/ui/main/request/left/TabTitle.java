package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

@Getter
public class TabTitle extends JPanel {
    private final String title;
    private final boolean hasContent;

    public TabTitle(
            String title,
            boolean hasContent
    ) {
        this.title = title;
        this.hasContent = hasContent;

        setLayout(new MigLayout("insets 0 0 0 0"));
        setOpaque(false);
        if(hasContent) add(new JLabel(new BxsCircleIcon(Color.ORANGE)));
        add(new JLabel(title));
    }
}
