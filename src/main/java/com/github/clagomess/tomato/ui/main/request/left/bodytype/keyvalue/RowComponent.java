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
import java.util.Objects;

@Getter
@Setter
class RowComponent extends JPanel {
    private final Container parent;
    private final RequestStagingMonitor requestStagingMonitor;
    private final List<RequestDto.KeyValueItem> list;
    private final RequestDto.KeyValueItem item;
    private OnChange onChange;

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
            RequestDto.KeyValueItem item,
            OnChange onChange
    ){
        this.parent = parent;
        this.requestStagingMonitor = requestStagingMonitor;
        this.list = list;
        this.item = item;
        this.onChange = onChange;

        if(!this.list.contains(this.item)){
            this.list.add(this.item);
        }

        // set values
        // @TODO: not set row disabled when fresh load and item is disabled
        cbSelected.setSelected(item.isSelected());
        txtKey.setText(item.getKey());
        txtValue.setText(item.getValue());

        // listeners
        cbSelected.addActionListener(l -> cbSelectedOnChange());
        txtKey.addOnChange(this::txtKeyOnChange);
        txtValue.addOnChange(this::txtValueOnChange);
        // @TODO: add possibility to expand to new window
        btnRemove.addActionListener(l -> remove());

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
        if(Objects.equals(item.isSelected(), cbSelected.isSelected())) return;

        item.setSelected(cbSelected.isSelected());
        setEnabled(cbSelected.isSelected());
        requestStagingMonitor.update();
        onChange.run(item);
    }

    private void txtKeyOnChange(String value){
        if(Objects.equals(value, item.getKey())) return;

        item.setKey(value);
        requestStagingMonitor.update();
        onChange.run(item);
    }

    private void txtValueOnChange(String value){
        if(Objects.equals(value, item.getValue())) return;

        item.setValue(value);
        requestStagingMonitor.update();
        onChange.run(item);
    }

    public void remove(){
        list.remove(item);
        requestStagingMonitor.update();
        onChange.run(null);
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

    @FunctionalInterface
    public interface OnChange {
        void run(RequestDto.KeyValueItem item);
    }
}
