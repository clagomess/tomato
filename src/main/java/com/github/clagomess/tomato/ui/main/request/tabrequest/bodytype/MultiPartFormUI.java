package com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype.multipartform.RowComponent;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;

public class MultiPartFormUI extends JPanel implements BodyTypeUI {
    private final RequestDto requestDto;
    private final JButton btnAddNew = new JButton("Add New");

    public MultiPartFormUI(RequestDto requestDto){
        this.requestDto = requestDto;
        if(this.requestDto.getBody().getMultiPartForm() == null){ //@TODO: ver na raiz, nunca nulo
            this.requestDto.getBody().setMultiPartForm(new ArrayList<>());
        }

        setLayout(new MigLayout("insets 0 0 0 0", "[grow, fill]", ""));

        JPanel header = new JPanel();
        header.setLayout(new MigLayout("insets 0 0 0 0", "[grow, fill]", ""));
        header.add(new JLabel("Type"));
        header.add(new JLabel("Key"), "width 120:120:120");
        header.add(new JLabel("Value"), "width 100%");
        header.add(new JLabel());
        header.add(new JLabel());

        add(btnAddNew, "span,wrap"); // @TODO: tmp
        add(header, "width 100%, wrap");

        btnAddNew.addActionListener(l -> {
            addRow(new RequestDto.MultiPartFormItem());
        });

        this.requestDto.getBody().getMultiPartForm().forEach(item -> {
            addRow(item);
        });
    }

    private void addRow(RequestDto.MultiPartFormItem item){
        add(new RowComponent(this, this.requestDto, item), "width 100%, wrap");
        revalidate();
        repaint();
    }
}
