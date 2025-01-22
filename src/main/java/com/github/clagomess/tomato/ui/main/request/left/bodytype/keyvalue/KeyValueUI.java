package com.github.clagomess.tomato.ui.main.request.left.bodytype.keyvalue;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class KeyValueUI extends JPanel {
    private final List<RequestDto.KeyValueItem> list;
    private final RequestStagingMonitor requestStagingMonitor;
    private final IconButton btnAddNew = new IconButton(new BxPlusIcon(), "Add new");
    private final JPanel rowsPanel;

    @Setter
    private RowComponent.OnChange onChange = item -> {};

    public KeyValueUI(
            List<RequestDto.KeyValueItem> list,
            RequestStagingMonitor requestStagingMonitor
    ) {
        this.list = list;
        this.requestStagingMonitor = requestStagingMonitor;

        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[grow,fill]"
        ));

        JPanel header = new JPanel(new MigLayout(
                "insets 5 0 5 0",
                "[][][grow,fill][]"
        ));
        header.setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#616365")));

        header.add(new JLabel(), "width 25!");
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
            var item = new RequestDto.KeyValueItem();
            addRow(item);
            onChange.run(item);
            requestStagingMonitor.update();
        });

        SwingUtilities.invokeLater(() -> {
            this.list.forEach(this::addRow);
        });
    }

    private void addRow(RequestDto.KeyValueItem item){
        var row = new RowComponent(
                rowsPanel,
                this.requestStagingMonitor,
                this.list,
                item,
                onChange
        );

        rowsPanel.add(row, "wrap");
        rowsPanel.revalidate();
    }

    public void refresh(){
        List<RowComponent> rows = Arrays.stream(rowsPanel.getComponents())
                .filter(row -> row instanceof RowComponent)
                .map(row -> (RowComponent) row)
                .toList();

        rows.forEach(row -> {
            row.setOnChange(value -> {});

            if(!list.contains(row.getItem())){
                row.remove();
            }

            row.getCbSelected().setSelected(row.getItem().isSelected());
            row.getTxtKey().setText(row.getItem().getKey());
            row.getTxtValue().setText(row.getItem().getValue());

            row.setOnChange(onChange);
        });

        // add
        for(var item : list){
            var ret = rows.stream().noneMatch(row -> item.equals(row.getItem()));
            if(ret) addRow(item);
        }
    }

    public void dispose(){
        Arrays.stream(rowsPanel.getComponents())
                .filter(row -> row instanceof RowComponent)
                .map(row -> (RowComponent) row)
                .forEach(RowComponent::dispose);
    }
}
