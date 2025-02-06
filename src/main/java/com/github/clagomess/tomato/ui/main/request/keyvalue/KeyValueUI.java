package com.github.clagomess.tomato.ui.main.request.keyvalue;

import com.github.clagomess.tomato.dto.data.KeyValueItemDto;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class KeyValueUI extends JPanel {
    private final List<KeyValueItemDto> listItens;
    private final RequestStagingMonitor requestStagingMonitor;
    private final IconButton btnAddNew = new IconButton(new BxPlusIcon(), "Add new");
    private final JPanel rowsPanel;
    private final KeyValueOptions options;

    public KeyValueUI(
            List<KeyValueItemDto> listItens,
            RequestStagingMonitor requestStagingMonitor
    ) {
        this(
                listItens,
                requestStagingMonitor,
                KeyValueOptions.builder().build()
        );
    }

    public KeyValueUI(
            List<KeyValueItemDto> listItens,
            RequestStagingMonitor requestStagingMonitor,
            KeyValueOptions options
    ){
        this.listItens = listItens;
        this.requestStagingMonitor = requestStagingMonitor;
        this.options = options;

        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[grow, fill]"
        ));

        // ### HEADER
        JPanel header = new JPanel(new MigLayout("insets 5 0 5 0"));
        header.setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#616365")));
        header.add(new JLabel(), "width 25!");

        if(options.isEnableTypeColumn()) {
            header.add(new JLabel("Type"), "width 70!");
        }

        header.add(new JLabel("Key"), "width 100!");
        header.add(new JLabel("Value"), "grow, width 100%");

        if(options.getCharsetComboBox() != null){
            header.add(options.getCharsetComboBox());
        }

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
            var item = new KeyValueItemDto();
            addRow(item);
            options.getOnChange().run(item);
            requestStagingMonitor.update();
        });

        SwingUtilities.invokeLater(() -> {
            this.listItens.forEach(this::addRow);
        });
    }

    private void addRow(KeyValueItemDto item){
        var row = new RowComponent(
                rowsPanel,
                this.requestStagingMonitor,
                this.listItens,
                item,
                options
        );

        rowsPanel.add(row, "wrap 4");
        rowsPanel.revalidate();
    }

    public void dispose(){
        Arrays.stream(rowsPanel.getComponents())
                .filter(row -> row instanceof RowComponent)
                .map(row -> (RowComponent) row)
                .forEach(RowComponent::dispose);
    }
}
