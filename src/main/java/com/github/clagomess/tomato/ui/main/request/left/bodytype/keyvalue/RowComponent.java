package com.github.clagomess.tomato.ui.main.request.left.bodytype.keyvalue;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.ui.component.ComponentUtil;
import com.github.clagomess.tomato.ui.component.ListenableTextField;
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
    private final ListenableTextField txtKey = new ListenableTextField();
    private final ListenableTextField txtValue = new ListenableTextField();
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
        txtKey.addOnChange(item::setKey);
        txtValue.addOnChange(item::setValue);
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
