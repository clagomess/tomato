package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;

import static com.github.clagomess.tomato.ui.component.IconFactory.createHttpMethodIcon;

public class TabTitleUI extends JPanel {
    private final JLabel changeIcon = new JLabel("-");
    private final JLabel httpMethod = new JLabel();
    private final JLabel title = new JLabel();
    private final JButton btnClose = new JButton("x"){{
        setBorder(BorderFactory.createEmptyBorder());
    }};

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

        requestPublisher.getOnSave().addListener(event -> {
            if(!requestDto.getId().equals(event.getId())) return;
            httpMethod.setIcon(createHttpMethodIcon(event.getMethod()));
            title.setText(event.getName());
        });

        requestPublisher.getOnChange().addListener(event -> {
            if(!event.id().equals(requestDto.getId())) return;

            changeIcon.setText(event.change() ? "*" : "o");
        });
    }

    public void onClose(ActionListener listener) {
        btnClose.addActionListener(listener);
    }
}
