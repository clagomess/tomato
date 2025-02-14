package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.dto.table.KeyValueTMDto;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import com.github.clagomess.tomato.ui.component.tablemanager.TableManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

class ViewInjectedEnvironmentFrame extends JFrame {
    public ViewInjectedEnvironmentFrame(
            Component parent,
            EnvMap envMap
    ) {
        setTitle("View Injected Environment");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(500, 200));
        setPreferredSize(new Dimension(500, 200));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        TableManager<KeyValueTMDto> tableManager = new TableManager<>(
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
