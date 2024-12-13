package com.github.clagomess.tomato.ui.main.request.left.bodytype.multipartform;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.BodyTypeUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public class MultiPartFormUI extends JPanel implements BodyTypeUI {
    private final List<RequestDto.KeyValueItem> multiPartFormItems;
    private final RequestStagingMonitor requestStagingMonitor;
    private final JButton btnAddNew = new JButton(new BxPlusIcon());

    public MultiPartFormUI(
            List<RequestDto.KeyValueItem> multiPartFormItems,
            RequestStagingMonitor requestStagingMonitor
    ){
        this.multiPartFormItems = multiPartFormItems;
        this.requestStagingMonitor = requestStagingMonitor;

        setLayout(new MigLayout("insets 0 0 0 0", "[grow, fill]", ""));

        JPanel header = new JPanel(new MigLayout(
                "insets 0 0 0 0",
                "[][][][grow, fill][]",
                ""
        ));
        header.add(new JLabel(), "width 25:25:25");
        header.add(new JLabel("Type"), "width 70:70:70");
        header.add(new JLabel("Key"), "width 100:100:100");
        header.add(new JLabel("Value"), "width 100%");
        header.add(btnAddNew);

        add(header, "width 100%, wrap");

        btnAddNew.addActionListener(l -> {
            addRow(new RequestDto.KeyValueItem());
            requestStagingMonitor.update();
        });

        this.multiPartFormItems.forEach(this::addRow);
    }

    private void addRow(RequestDto.KeyValueItem item){
        var row = new RowComponent(
                this,
                this.requestStagingMonitor,
                this.multiPartFormItems,
                item
        );

        add(row, "width 100%, wrap");
        revalidate();
        repaint();
    }
}
