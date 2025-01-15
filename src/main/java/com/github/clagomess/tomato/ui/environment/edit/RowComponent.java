package com.github.clagomess.tomato.ui.environment.edit;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.ui.component.ComponentUtil;
import com.github.clagomess.tomato.ui.component.ListenableTextField;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
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
    private final List<EnvironmentDto.Env> list;
    private final EnvironmentDto.Env item;

    private final ListenableTextField txtKey = new ListenableTextField();
    private final ListenableTextField txtValue = new ListenableTextField();
    private final JButton btnRemove = new JButton(new BxTrashIcon()){{
        setToolTipText("Remove");
    }};

    public RowComponent(
            Container parent,
            List<EnvironmentDto.Env> list,
            EnvironmentDto.Env item
    ){
        this.parent = parent;
        this.list = list;
        this.item = item;

        if(!this.list.contains(this.item)){
            this.list.add(this.item);
        }

        // set values
        // @TODO: not set row disabled when fresh load and item is disabled
        txtKey.setText(item.getKey());
        txtValue.setText(item.getValue());

        // listeners
        txtKey.addOnChange(value -> {
            item.setKey(value);
        });
        txtValue.addOnChange(value -> {
            item.setValue(value);
        });
        btnRemove.addActionListener(l -> btnRemoveAction());

        // layout
        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[][grow, fill][]"
        ));
        add(txtKey, "width 150!");
        add(txtValue, "width 150:150:100%");
        add(btnRemove);
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
