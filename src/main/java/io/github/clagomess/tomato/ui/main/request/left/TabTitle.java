package io.github.clagomess.tomato.ui.main.request.left;

import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;

@Getter
public class TabTitle extends JPanel {
    private static final Icon HAS_CONTENT_ICON = new BxsCircleIcon(Color.ORANGE);

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
        ComponentUtil.checkIsEventDispatchThread();

        removeAll();

        if(this.hasContent){
            add(new JLabel(HAS_CONTENT_ICON));
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
