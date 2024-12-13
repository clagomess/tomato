package com.github.clagomess.tomato.ui.main.request.left.bodytype.multipartform;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxPlusIcon;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.BodyTypeUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class MultiPartFormUI extends JPanel implements BodyTypeUI {
    private final RequestDto requestDto;
    private final JButton btnAddNew = new JButton(new BxPlusIcon());

    public MultiPartFormUI(RequestDto requestDto){
        this.requestDto = requestDto;

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
        });

        this.requestDto.getBody().getMultiPartForm()
                .forEach(this::addRow);
    }

    private void addRow(RequestDto.KeyValueItem item){
        add(new RowComponent(this, this.requestDto, item), "width 100%, wrap");
        revalidate();
        repaint();
    }
}
