package com.github.clagomess.tomato.ui.main.request.left.bodytype.keyvalue;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.BodyTypeUI;
import jakarta.annotation.Nonnull;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public class KeyValueUI extends JPanel implements BodyTypeUI {
    private final List<RequestDto.KeyValueItem> list;
    private final JButton btnAddNew = new JButton(new BxPlusIcon());

    public KeyValueUI(
            @Nonnull List<RequestDto.KeyValueItem> list,
            @Nonnull RequestStagingMonitor requestStagingMonitor
    ) {
        this.list = list;

        setLayout(new MigLayout("insets 0 0 0 0", "[grow, fill]", ""));

        JPanel header = new JPanel(new MigLayout(
                "insets 0 0 0 0",
                "[][][grow, fill][]",
                ""
        ));
        header.add(new JLabel(), "width 25:25:25");
        header.add(new JLabel("Key"), "width 100:100:100");
        header.add(new JLabel("Value"), "width 100%");
        header.add(btnAddNew);

        add(header, "width 100%, wrap");

        btnAddNew.addActionListener(l -> {
            addRow(new RequestDto.KeyValueItem());
        });

        this.list.forEach(this::addRow);

        //@TODO: impl requestChangeDto
    }

    private void addRow(RequestDto.KeyValueItem item){
        add(new RowComponent(this, this.list, item), "width 100%, wrap");
        revalidate();
        repaint();
    }
}
