package com.github.clagomess.tomato.ui.main.request.right.cookie;

import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSaveIcon;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

@Getter
@Setter
class Row extends JPanel {
    private static final Icon SAVE_ICON = new BxSaveIcon();

    private final Container parent;
    private final TabKey tabKey;
    private final Map.Entry<String, String> item;
    private final JButton btnSave = new IconButton(SAVE_ICON, "Set Cookie");

    public Row(
            Container parent,
            TabKey tabKey,
            Map.Entry<String, String> item
    ){
        this.parent = parent;
        this.tabKey = tabKey;
        this.item = item;

        // listeners
        btnSave.addActionListener(l -> btnSaveAction());

        // layout
        setLayout(new MigLayout(
                "insets 2",
                "[][grow, fill][]"
        ));
        add(new JTextField(item.getKey()), "width 150!");
        add(new JTextField(item.getValue()), "width 150:150:100%");
        add(btnSave);
    }

    private void btnSaveAction(){
        RequestPublisher.getInstance()
                .getOnCookieSet()
                .publish(tabKey, new KeyValueItemDto(item.getKey(), item.getValue()));
    }
}
