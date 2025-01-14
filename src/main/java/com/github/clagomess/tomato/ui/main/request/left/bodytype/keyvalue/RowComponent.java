package com.github.clagomess.tomato.ui.main.request.left.bodytype.keyvalue;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.ui.component.ComponentUtil;
import com.github.clagomess.tomato.ui.component.ListenableTextField;
import com.github.clagomess.tomato.ui.component.envtextfield.EnvTextField;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Getter
@Setter
class RowComponent extends JPanel {
    private final Container parent;
    private final RequestStagingMonitor requestStagingMonitor;
    private final List<RequestDto.KeyValueItem> list;
    private final RequestDto.KeyValueItem item;

    private final JCheckBox cbSelected = new JCheckBox();
    private final ListenableTextField txtKey = new ListenableTextField();
    private final EnvTextField txtValue = new EnvTextField();
    private final JButton btnRemove = new JButton(new BxTrashIcon()){{
        setToolTipText("Remove");
    }};

    public RowComponent(
            Container parent,
            RequestStagingMonitor requestStagingMonitor,
            List<RequestDto.KeyValueItem> list,
            RequestDto.KeyValueItem item
    ){
        this.parent = parent;
        this.requestStagingMonitor = requestStagingMonitor;
        this.list = list;
        this.item = item;

        if(!this.list.contains(this.item)){
            this.list.add(this.item);
        }

        // set values
        cbSelected.setSelected(item.isSelected());
        txtKey.setText(item.getKey());
        txtValue.setText(item.getValue());

        // listeners
        cbSelected.addActionListener(l -> cbSelectedOnChange());
        txtKey.addOnChange(value -> {
            item.setKey(value);
            requestStagingMonitor.update();
        });
        txtValue.addOnChange(value -> {
            item.setValue(value);
            requestStagingMonitor.update();
        });
        // @TODO: add possibility to expand to new window
        btnRemove.addActionListener(l -> btnRemoveAction());

        // layout
        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[][][grow,fill][]"
        ));
        add(cbSelected);
        add(txtKey, "width 150!");
        add(txtValue, "width 150:150:100%");
        add(btnRemove);
    }

    private void cbSelectedOnChange(){
        item.setSelected(cbSelected.isSelected());
        setEnabled(cbSelected.isSelected());
        requestStagingMonitor.update();
    }

    private void btnRemoveAction(){
        list.remove(item);
        requestStagingMonitor.update();
        txtValue.dispose();
        parent.remove(ComponentUtil.getComponentIndex(parent, this));
        parent.revalidate();
        parent.repaint();
    }

    public void setEnabled(boolean enabled){
        txtKey.setEnabled(enabled);
        txtValue.setEnabled(enabled);
        btnRemove.setEnabled(enabled);
    }
}
