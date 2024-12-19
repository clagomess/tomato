package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.dto.table.KeyValueTMDto;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImageIcon;
import com.github.clagomess.tomato.ui.component.tablemanager.TableManagerUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ViewInjectedEnvironmentUI extends JFrame {
    public ViewInjectedEnvironmentUI(
            Component parent,
            EnvDocumentListener.EnvMap envMap
    ) {
        setTitle("View Injected Environment");
        setIconImage(new FaviconImageIcon().getImage());
        setMinimumSize(new Dimension(500, 200));
        setPreferredSize(new Dimension(500, 200));
        setResizable(true);

        TableManagerUI<KeyValueTMDto> tableManager = new TableManagerUI<>(
                KeyValueTMDto.class
        );

        envMap.getInjected().forEach((key, value) -> {
            tableManager.getModel().addRow(new KeyValueTMDto(key, value));
        });

        JPanel panel = new JPanel(new MigLayout(
                "insets 10",
                "[grow, fill]"
        ));
        panel.add(new JScrollPane(tableManager.getTable()), "height 100%");

        add(panel);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
