package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxXIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TabTitleUI extends JPanel {
    private final BxsCircleIcon iconHasChanged = new BxsCircleIcon(Color.ORANGE);
    private final BxsCircleIcon iconHasNotChanged = new BxsCircleIcon(Color.GRAY);

    private final JLabel changeIcon = new JLabel();
    private final JLabel httpMethod = new JLabel();
    private final JLabel title = new JLabel();
    private final JButton btnClose = new JButton(new BxXIcon()){{
        setBorder(BorderFactory.createEmptyBorder());
        setToolTipText("Close");
    }};

    private final List<UUID> listenerUuid = new ArrayList<>(2);
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public TabTitleUI(
            TabKey tabKey,
            RequestDto requestDto
    ){
        setLayout(new MigLayout("insets 0 0 0 0"));
        setOpaque(false);
        add(changeIcon);
        add(httpMethod);
        add(title, "width ::200");
        add(btnClose);

        // set data
        httpMethod.setIcon(requestDto.getMethod().getIcon());
        title.setText(requestDto.getName());

        listenerUuid.add(requestPublisher.getOnSave().addListener(requestDto.getId(), event -> {
            httpMethod.setIcon(event.getMethod().getIcon());
            title.setText(event.getName());
        }));

        listenerUuid.add(requestPublisher.getOnStaging().addListener(
                tabKey,
                event -> changeIcon.setIcon(event ? iconHasChanged : iconHasNotChanged)
        ));
    }

    public void onClose(ActionListener listener) {
        btnClose.addActionListener(listener);
        btnClose.addActionListener(l -> dispose());
    }

    public void dispose(){
        listenerUuid.forEach(uuid -> {
            requestPublisher.getOnSave().removeListener(uuid);
            requestPublisher.getOnStaging().removeListener(uuid);
        });
    }
}
