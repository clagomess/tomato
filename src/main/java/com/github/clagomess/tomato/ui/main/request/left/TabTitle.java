package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isEventDispatchThread;

@Getter
public class TabTitle extends JPanel {
    private final String title;
    private boolean hasContent;

    public TabTitle(
            TabKey tabKey,
            String title,
            HasContentFI checkHasContent
    ) {
        this.title = title;
        this.hasContent = checkHasContent.get();

        setLayout(new MigLayout("insets 0 0 0 0"));
        setOpaque(false);
        refreshTitleIcon();

        RequestPublisher.getInstance()
                .getOnStaging()
                .addListener(tabKey, event -> {
                    if(checkHasContent.get() == hasContent) return;
                    hasContent = !hasContent;
                    invokeLater(this::refreshTitleIcon);
                });
    }

    private void refreshTitleIcon(){
        if(!isEventDispatchThread()) throw new IllegalThreadStateException();

        removeAll();

        if(this.hasContent){
            add(new JLabel(new BxsCircleIcon(Color.ORANGE)));
        }

        add(new JLabel(title));

        revalidate();
        repaint();
    }

    @FunctionalInterface
    public interface HasContentFI {
        boolean get();
    }
}
