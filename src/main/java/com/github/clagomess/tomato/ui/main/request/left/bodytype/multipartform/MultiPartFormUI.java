package com.github.clagomess.tomato.ui.main.request.left.bodytype.multipartform;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.List;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class MultiPartFormUI extends JPanel {
    private final List<RequestDto.KeyValueItem> multiPartFormItems;
    private final RequestStagingMonitor requestStagingMonitor;
    private final JButton btnAddNew = new JButton(new BxPlusIcon());
    private final JPanel rowsPanel;

    public MultiPartFormUI(
            List<RequestDto.KeyValueItem> multiPartFormItems,
            RequestStagingMonitor requestStagingMonitor
    ){
        this.multiPartFormItems = multiPartFormItems;
        this.requestStagingMonitor = requestStagingMonitor;

        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[grow, fill]"
        ));

        JPanel header = new JPanel(new MigLayout(
                "insets 5 0 5 0",
                "[][][][grow, fill][]",
                ""
        ));
        header.setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#616365")));
        header.add(new JLabel(), "width 25!");
        header.add(new JLabel("Type"), "width 70!");
        header.add(new JLabel("Key"), "width 100!");
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
            addRow(new RequestDto.KeyValueItem());
            requestStagingMonitor.update();
        });

        SwingUtilities.invokeLater(() -> {
            this.multiPartFormItems.forEach(this::addRow);
        });
    }

    private void addRow(RequestDto.KeyValueItem item){
        var row = new RowComponent(
                rowsPanel,
                this.requestStagingMonitor,
                this.multiPartFormItems,
                item
        );

        rowsPanel.add(row, "wrap");
        rowsPanel.revalidate();
    }
}
