package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class TabTitleUI extends JPanel {
    @Getter
    private final String title;

    @Getter
    private boolean hasContent;

    private final BxsCircleIcon iconHasContent = new BxsCircleIcon(Color.ORANGE);
    private final JLabel contentIcon = new JLabel();

    public TabTitleUI(
            String title,
            boolean hasContent
    ) {
        this.title = title;
        this.hasContent = hasContent;

        setLayout(new MigLayout("insets 0 0 0 0"));
        setOpaque(false);
        add(contentIcon);
        add(new JLabel(title));

        // setData
        contentIcon.setIcon(hasContent ? iconHasContent : null);
    }
}
