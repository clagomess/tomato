package io.github.clagomess.tomato.ui.component.envtextfield;

import io.github.clagomess.tomato.controller.component.envtextfield.ViewInjectedEnvironmentFrameController;
import io.github.clagomess.tomato.dto.table.KeyValueTMDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.ui.BaseFrame;
import io.github.clagomess.tomato.ui.component.tablemanager.TableManager;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

class ViewInjectedEnvironmentFrame extends BaseFrame {
    private final ViewInjectedEnvironmentFrameController controller = new ViewInjectedEnvironmentFrameController();

    public ViewInjectedEnvironmentFrame(
            Component parent,
            @Nullable RequestHeadDto requestHead,
            Map<String, String> injected
    ) {
        setTitle("View Injected Environment");
        setMinimumSize(new Dimension(500, 200));
        setPreferredSize(new Dimension(500, 200));
        setResizable(true);

        TableManager<KeyValueTMDto> tableManager = new TableManager<>(
                KeyValueTMDto.class
        );

        controller.getKeyValueStream(requestHead, injected)
                .forEach(item -> tableManager.getModel().addRow(item));

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
