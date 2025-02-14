package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.controller.main.request.TabTitleController;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxXIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class TabTitle extends JPanel {
    protected final BxsCircleIcon iconHasChanged = new BxsCircleIcon(Color.ORANGE);
    protected final BxsCircleIcon iconHasNotChanged = new BxsCircleIcon(Color.GRAY);

    protected final JLabel changeIcon = new JLabel();
    protected final JLabel httpMethod = new JLabel();
    protected final JLabel title = new JLabel();
    private final IconButton btnClose = new IconButton(new BxXIcon(), "Close");

    private final TabTitleController controller = new TabTitleController();

    public TabTitle(
            @Nullable RequestTabbedPane parent,
            @NotNull RequestSplitPane requestSplitPane,
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
        addMouseListener(new TabTitleMouseListener(requestSplitPane.getKey(), parent));
        setToolTipText(request.getName());

        // set data
        httpMethod.setIcon(request.getMethod().getIcon());
        title.setText(request.getName());
        btnClose.addActionListener(onClose);
        btnClose.addActionListener(l -> dispose());

        if(requestSplitPane.getRequestStagingMonitor().isDiferent()){
            changeIcon.setIcon(iconHasChanged);
        }

        controller.addOnChangeListener(requestHead, (method, name) -> {
            httpMethod.setIcon(method.getIcon());
            title.setText(name);
        });

        controller.addOnStagingListener(requestSplitPane.getKey(), hasChanged -> {
            changeIcon.setIcon(hasChanged ? iconHasChanged : iconHasNotChanged);
        });
    }

    public void dispose(){
        controller.dispose();
    }
}
