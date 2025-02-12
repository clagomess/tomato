package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxXIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TabTitleUI extends JPanel {
    protected final BxsCircleIcon iconHasChanged = new BxsCircleIcon(Color.ORANGE);
    protected final BxsCircleIcon iconHasNotChanged = new BxsCircleIcon(Color.GRAY);

    protected final JLabel changeIcon = new JLabel();
    protected final JLabel httpMethod = new JLabel();
    protected final JLabel title = new JLabel();
    private final IconButton btnClose = new IconButton(new BxXIcon(), "Close");

    private final RequestKey requestKey;
    private final TabKey tabKey;
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public TabTitleUI(
            RequestTabPaneUI parent,
            TabKey tabKey,
            RequestHeadDto requestHead
    ){
        this.tabKey = tabKey;
        this.requestKey = new RequestKey(requestHead);

        setLayout(new MigLayout("insets 0 0 0 0"));
        setOpaque(false);
        add(changeIcon);
        add(httpMethod);
        add(title, "width ::200");
        add(btnClose);
        addMouseListener(new TabTitleMouseListener(tabKey, parent));
        setToolTipText(requestHead.getName());

        // set data
        httpMethod.setIcon(requestHead.getMethod().getIcon());
        title.setText(requestHead.getName());

        requestPublisher.getOnChange().addListener(requestKey, event -> {
            httpMethod.setIcon(event.getEvent().getMethod().getIcon());
            title.setText(event.getEvent().getName());
        });

        requestPublisher.getOnStaging().addListener(
                tabKey,
                event -> changeIcon.setIcon(event ? iconHasChanged : iconHasNotChanged)
        );
    }

    public void onClose(ActionListener listener) {
        btnClose.addActionListener(listener);
        btnClose.addActionListener(l -> dispose());
    }

    public void dispose(){
        requestPublisher.getOnChange().removeListener(requestKey);
        requestPublisher.getOnStaging().removeListener(tabKey);
    }
}
