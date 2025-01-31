package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.dto.table.KeyValueTMDto;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import com.github.clagomess.tomato.ui.component.tablemanager.TableManagerUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

class ViewInjectedEnvironmentUI extends JFrame {
    public ViewInjectedEnvironmentUI(
            Component parent,
            EnvMap envMap
    ) {
        setTitle("View Injected Environment");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(500, 200));
        setPreferredSize(new Dimension(500, 200));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        TableManagerUI<KeyValueTMDto> tableManager = new TableManagerUI<>(
                KeyValueTMDto.class
        );

        envMap.getInjected().forEach((key, value) -> {
            tableManager.getModel().addRow(new KeyValueTMDto(key, value));
        });

        setLayout(new MigLayout(
                "insets 10",
                "[grow, fill]"
        ));
        add(new JScrollPane(tableManager.getTable()), "height 100%");

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
