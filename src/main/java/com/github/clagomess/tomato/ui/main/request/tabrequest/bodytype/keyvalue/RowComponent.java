package com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype.keyvalue;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.util.ComponentUtil;
import jakarta.annotation.Nonnull;
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
    private final List<RequestDto.KeyValueItem> list;
    private final RequestDto.KeyValueItem item;

    private final JCheckBox cbSelected = new JCheckBox();
    private final JTextField txtKey = new JTextField();
    private final JTextField txtValue = new JTextField();
    private final JButton btnRemove = new JButton("x"); //@TODO: change to trash icon;

    public RowComponent(
            @Nonnull Container parent,
            @Nonnull List<RequestDto.KeyValueItem> list,
            @Nonnull RequestDto.KeyValueItem item
    ){
        this.parent = parent;
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
        txtKey.addActionListener(l -> txtKeyOnChange());
        txtValue.addActionListener(l -> txtValueOnChange());
        btnRemove.addActionListener(l -> btnRemoveAction());

        // layout
        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[][][grow, fill][]",
                ""
        ));
        add(cbSelected, "width 25:25:25");
        add(txtKey, "width 100:100:100");
        add(txtValue, "width 100%");
        add(btnRemove, "wrap");
    }

    private void cbSelectedOnChange(){
        item.setSelected(cbSelected.isSelected());
        setEnabled(cbSelected.isSelected());
    }

    private void txtKeyOnChange(){
        item.setKey(txtKey.getText());
    }

    private void txtValueOnChange(){
        item.setKey(txtKey.getText());
    }

    private void btnRemoveAction(){
        list.remove(item);
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
