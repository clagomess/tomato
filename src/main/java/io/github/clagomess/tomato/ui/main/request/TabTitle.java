package io.github.clagomess.tomato.ui.main.request;

import io.github.clagomess.tomato.controller.main.request.TabTitleController;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.ui.component.IconButton;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxXIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;
import io.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import io.github.clagomess.tomato.ui.main.request.popupmenu.TabTitleMouseListener;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static javax.swing.SwingUtilities.invokeLater;

class TabTitle extends JPanel implements TabTitleInterface {
    private static final Icon HAS_CHANGED_ICON = new BxsCircleIcon(Color.ORANGE);
    private static final Icon HAS_NOT_CHANGED_ICON = new BxsCircleIcon(Color.GRAY);
    private static final Icon X_ICON = new BxXIcon();

    protected final JLabel changeIcon = new JLabel();
    protected final JLabel httpMethod = new JLabel();
    protected final JLabel title = new JLabel();
    private final IconButton btnClose = new IconButton(X_ICON, "Close");

    private final TabTitleController controller;

    public TabTitle(
            @Nullable RequestTabbedPane parent,
            @NonNull TabKey key,
            @NonNull RequestStagingMonitor requestStagingMonitor,
            @Nullable RequestHeadDto requestHead,
            @NonNull RequestDto request,
            @NonNull ActionListener onClose
    ){
        this.controller = new TabTitleController(this, requestHead);

        setLayout(new MigLayout("insets 0 0 0 0"));
        setOpaque(false);
        add(changeIcon);
        add(httpMethod);
        add(title, "width ::200");
        add(btnClose);
        addMouseListener(new TabTitleMouseListener(key, parent, controller.getRequestHeadAR()));
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

        controller.addOnChangeListener(request);
        controller.addOnStagingListener(key);
    }

    public void refresh(RequestHeadDto requestHead){
        invokeLater(() -> {
            httpMethod.setIcon(requestHead.getMethod().getIcon());
            title.setText(requestHead.getName());
        });
    }

    public void setIcon(boolean hasChanged){
        invokeLater(() -> changeIcon.setIcon(
                hasChanged ? HAS_CHANGED_ICON : HAS_NOT_CHANGED_ICON
        ));
    }

    public void dispose(){
        controller.dispose();
    }
}
