package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.clagomess.tomato.ui.component.IconFactory.createHttpMethodIcon;

public class TabTitleUI extends JPanel {
    private final JLabel changeIcon = new JLabel("-");
    private final JLabel httpMethod = new JLabel();
    private final JLabel title = new JLabel();
    private final JButton btnClose = new JButton("x"){{
        setBorder(BorderFactory.createEmptyBorder());
    }};

    private final List<UUID> listenerUuid = new ArrayList<>(2);
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public TabTitleUI(
            RequestDto requestDto
    ){
        setLayout(new MigLayout("insets 0 0 0 0"));
        setOpaque(false);
        add(changeIcon);
        add(httpMethod);
        add(title);
        add(btnClose);

        // set data
        httpMethod.setIcon(createHttpMethodIcon(requestDto.getMethod()));
        title.setText(requestDto.getName());

        listenerUuid.add(requestPublisher.getOnSave().addListener(requestDto.getId(), event -> {
            httpMethod.setIcon(createHttpMethodIcon(event.getMethod()));
            title.setText(event.getName());
        }));

        listenerUuid.add(requestPublisher.getOnChange().addListener(
                requestDto.getId(),
                event -> changeIcon.setText(event ? "*" : "o")
        ));
    }

    public void onClose(ActionListener listener) {
        btnClose.addActionListener(listener);
        btnClose.addActionListener(l -> dispose());
    }

    public void dispose(){
        listenerUuid.forEach(uuid -> {
            requestPublisher.getOnSave().removeListener(uuid);
            requestPublisher.getOnChange().removeListener(uuid);
        });
    }
}
