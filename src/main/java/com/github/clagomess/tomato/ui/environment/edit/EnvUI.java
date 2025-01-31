package com.github.clagomess.tomato.ui.environment.edit;

import com.github.clagomess.tomato.dto.data.KeyValueItemDto;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.SwingUtilities.getAncestorOfClass;

public class EnvUI extends JPanel {
    private final List<KeyValueItemDto> list;
    private final IconButton btnAddNew = new IconButton(new BxPlusIcon(), "Add a new environment");
    private final JPanel rowsPanel;

    public EnvUI(
            List<KeyValueItemDto> list
    ) {
        this.list = list;

        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[grow, fill]"
        ));

        JPanel header = new JPanel(new MigLayout(
                "insets 5 0 5 0",
                "[][][grow, fill][]"
        ));
        header.setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#616365")));
        header.add(new JLabel(), "width 8!");
        header.add(new JLabel("Key"), "width 150!");
        header.add(new JLabel("Value"), "width 100%");
        header.add(btnAddNew);

        add(header, "width 100%, wrap");

        // ### ROWS
        rowsPanel = new JPanel(new MigLayout(
                "insets 0 0 0 0",
                "[grow,fill]"
        ));
        JScrollPane scrollPane = new JScrollPane(
                rowsPanel,
                VERTICAL_SCROLLBAR_AS_NEEDED,
                HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, "width ::100%, height 100%");
        btnAddNew.addActionListener(l -> {
            addRow(new KeyValueItemDto());

            var parent = (EnvironmentEditUI) getAncestorOfClass(EnvironmentEditUI.class, this);
            parent.updateStagingMonitor();
        });

        SwingUtilities.invokeLater(() -> {
            this.list.forEach(this::addRow);
        });
    }

    private void addRow(KeyValueItemDto item){
        var row = new RowComponent(
                rowsPanel,
                this.list,
                item
        );

        rowsPanel.add(row, "wrap");
        rowsPanel.revalidate();
    }

    public void resetStagingMonitor(){
        Arrays.stream(rowsPanel.getComponents())
                .filter(row -> row instanceof RowComponent)
                .map(row -> (RowComponent) row)
                .forEach(RowComponent::resetStagingMonitor);
    }
}
