package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.controller.main.request.TabTitleController;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxXIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class TabTitle extends JPanel {
    private static final Icon HAS_CHANGED_ICON = new BxsCircleIcon(Color.ORANGE);
    private static final Icon HAS_NOT_CHANGED_ICON = new BxsCircleIcon(Color.GRAY);
    private static final Icon X_ICON = new BxXIcon();

    protected final JLabel changeIcon = new JLabel();
    protected final JLabel httpMethod = new JLabel();
    protected final JLabel title = new JLabel();
    private final IconButton btnClose = new IconButton(X_ICON, "Close");

    private final TabTitleController controller = new TabTitleController();

    public TabTitle(
            @Nullable RequestTabbedPane parent,
            @NotNull TabKey key,
            @NotNull RequestStagingMonitor requestStagingMonitor,
            @Nullable RequestHeadDto requestHead,
            @NotNull RequestDto request,
            @NotNull ActionListener onClose
    ){
        setLayout(new MigLayout("insets 0 0 0 0"));
        setOpaque(false);
        add(changeIcon);
        add(httpMethod);
        add(title, "width ::200");
        add(btnClose);
        addMouseListener(new TabTitleMouseListener(key, parent));
        setToolTipText(request.getName());

        // set data
        httpMethod.setIcon(request.getMethod().getIcon());
        title.setText(request.getName());
        btnClose.addActionListener(onClose);
        btnClose.addActionListener(l -> dispose());
        changeIcon.setIcon(requestStagingMonitor.isDiferent() ?
                HAS_CHANGED_ICON :
                HAS_NOT_CHANGED_ICON
        );

        controller.addOnChangeListener(requestHead, (method, name) -> {
            httpMethod.setIcon(method.getIcon());
            title.setText(name);
        });

        controller.addOnStagingListener(key, hasChanged ->
            changeIcon.setIcon(hasChanged ? HAS_CHANGED_ICON : HAS_NOT_CHANGED_ICON)
        );
    }

    public void dispose(){
        controller.dispose();
    }
}
