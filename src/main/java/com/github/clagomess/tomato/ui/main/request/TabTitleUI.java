package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxXIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TabTitleUI extends JPanel {
    protected final BxsCircleIcon iconHasChanged = new BxsCircleIcon(Color.ORANGE);
    protected final BxsCircleIcon iconHasNotChanged = new BxsCircleIcon(Color.GRAY);

    protected final JLabel changeIcon = new JLabel();
    protected final JLabel httpMethod = new JLabel();
    protected final JLabel title = new JLabel();
    private final IconButton btnClose = new IconButton(new BxXIcon(), "Close");

    private final List<Runnable> dispose = new ArrayList<>(2);

    public TabTitleUI(
            @Nullable RequestTabPaneUI parent,
            @NotNull TabKey tabKey,
            @Nullable RequestHeadDto requestHead,
            @NotNull RequestDto request
    ){
        setLayout(new MigLayout("insets 0 0 0 0"));
        setOpaque(false);
        add(changeIcon);
        add(httpMethod);
        add(title, "width ::200");
        add(btnClose);
        addMouseListener(new TabTitleMouseListener(tabKey, parent));
        setToolTipText(request.getName());

        // set data
        httpMethod.setIcon(request.getMethod().getIcon());
        title.setText(request.getName());

        addOnChangeListener(requestHead);
        addOnStagingListener(tabKey);
    }

    protected void addOnChangeListener(
            @Nullable RequestHeadDto requestHead
    ){
        if(requestHead == null) return;

        var key = new RequestKey(requestHead);

        RequestPublisher.getInstance()
                .getOnChange()
                .addListener(key, event -> {
                    httpMethod.setIcon(event.getEvent().getMethod().getIcon());
                    title.setText(event.getEvent().getName());
                });

        dispose.add(() -> RequestPublisher.getInstance()
                .getOnChange()
                .removeListener(key));
    }

    protected void addOnStagingListener(
            @NotNull TabKey tabKey
    ){
        RequestPublisher.getInstance()
                .getOnStaging()
                .addListener(tabKey, event ->
                        changeIcon.setIcon(event ? iconHasChanged : iconHasNotChanged)
        );

        dispose.add(() -> RequestPublisher.getInstance()
                .getOnStaging()
                .removeListener(tabKey));
    }

    public void onClose(ActionListener listener) {
        btnClose.addActionListener(listener);
        btnClose.addActionListener(l -> dispose());
    }

    public void dispose(){
        dispose.forEach(Runnable::run);
    }
}
