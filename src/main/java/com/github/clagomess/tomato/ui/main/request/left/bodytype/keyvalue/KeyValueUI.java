package com.github.clagomess.tomato.ui.main.request.left.bodytype.keyvalue;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import jakarta.annotation.Nonnull;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.List;

public class KeyValueUI extends JPanel {
    private final List<RequestDto.KeyValueItem> list;
    private final RequestStagingMonitor requestStagingMonitor;
    private final JButton btnAddNew = new JButton(new BxPlusIcon());

    public KeyValueUI(
            @Nonnull List<RequestDto.KeyValueItem> list,
            @Nonnull RequestStagingMonitor requestStagingMonitor
    ) {
        this.list = list;
        this.requestStagingMonitor = requestStagingMonitor;

        setLayout(new MigLayout("insets 0 0 0 0", "[grow, fill]", ""));

        JPanel header = new JPanel(new MigLayout(
                "insets 5 0 5 0",
                "[][][grow, fill][]",
                ""
        ));
        header.setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#616365")));

        header.add(new JLabel(), "width 25:25:25");
        header.add(new JLabel("Key"), "width 100:100:100");
        header.add(new JLabel("Value"), "width 100%");
        header.add(btnAddNew);

        add(header, "width 100%, wrap");

        btnAddNew.addActionListener(l -> {
            addRow(new RequestDto.KeyValueItem());
            requestStagingMonitor.update();
        });

        this.list.forEach(this::addRow);
    }

    private void addRow(RequestDto.KeyValueItem item){
        var row = new RowComponent(
                this,
                this.requestStagingMonitor,
                this.list,
                item
        );

        add(row, "width 100%, wrap 0");
        revalidate();
        repaint();
    }
}
