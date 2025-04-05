package com.github.clagomess.tomato.ui.main.request.keyvalue;

import com.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum;
import com.github.clagomess.tomato.ui.component.ComponentUtil;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.ListenableTextField;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

@Slf4j
@Getter
@Setter
class RowComponent<T extends KeyValueItemDto> extends JPanel {
    private static final Icon TRASH_ICON = new BxTrashIcon();

    private final Container parent;
    private final RequestStagingMonitor requestStagingMonitor;
    private final List<T> listItens;
    private final T item;
    private KeyValueOptions options;

    private final JComboBox<KeyValueTypeEnum> cbType = new JComboBox<>(
            KeyValueTypeEnum.values()
    );
    private final ListenableTextField txtKey = new ListenableTextField();
    private ValueComponent<T> cValue;
    private final JCheckBox cbSelected = new JCheckBox();
    private final JButton btnRemove = new IconButton(TRASH_ICON, "Remove");

    public RowComponent(
            Container parent,
            RequestStagingMonitor requestStagingMonitor,
            List<T> listItens,
            T item,
            KeyValueOptions options
    ){
        this.parent = parent;
        this.requestStagingMonitor = requestStagingMonitor;
        this.listItens = listItens;
        this.item = item;
        this.options = options;

        if(!this.listItens.contains(this.item)){
            this.listItens.add(this.item);
        }

        setLayout(new MigLayout(
                "insets 2",
                item instanceof FileKeyValueItemDto ?
                        "[][][][grow, fill][]" :
                        "[][][grow, fill][]"
        ));

        // set values & add layout
        cbSelected.setSelected(item.isSelected());
        cbSelected.addActionListener(l -> cbSelectedOnChange());
        add(cbSelected);

        if(item instanceof FileKeyValueItemDto fvItem){
            cbType.setSelectedItem(fvItem.getType());
            cbType.setEnabled(item.isSelected());
            cbType.addActionListener(l -> cbTypeOnChange(fvItem));
            add(cbType, "width 70!");
        }

        txtKey.setText(item.getKey());
        txtKey.setEnabled(item.isSelected());
        txtKey.addOnChange(this::txtKeyOnChange);
        add(txtKey, "width 100!");

        cValue = new ValueComponent<>(requestStagingMonitor, options, item);
        cValue.getComponent().setEnabled(item.isSelected());
        add(cValue.getComponent(), "width 100:100:100%");

        btnRemove.setEnabled(item.isSelected());
        btnRemove.addActionListener(l -> btnRemoveAction());
        add(btnRemove);
    }

    private void cbTypeOnChange(FileKeyValueItemDto fvItem){
        KeyValueTypeEnum selectedType = (KeyValueTypeEnum) cbType.getSelectedItem();
        if(Objects.equals(selectedType, fvItem.getType())) return;

        fvItem.setType(selectedType);
        requestStagingMonitor.update();

        int index = ComponentUtil.getComponentIndex(this, cValue.getComponent());
        remove(index);

        cValue = new ValueComponent<>(requestStagingMonitor, options, item);

        add(cValue.getComponent(), "width 100:100:100%", index);
        revalidate();
        repaint();
    }

    private void cbSelectedOnChange(){
        if(Objects.equals(item.isSelected(), cbSelected.isSelected())) return;

        item.setSelected(cbSelected.isSelected());
        requestStagingMonitor.update();
        setEnabled(cbSelected.isSelected());
        requestStagingMonitor.update();
        options.getOnChange().run(item);
    }

    private void txtKeyOnChange(String value){
        if(Objects.equals(value, item.getKey())) return;

        item.setKey(value);
        requestStagingMonitor.update();
        options.getOnChange().run(item);
    }

    private void btnRemoveAction(){
        listItens.remove(item);
        requestStagingMonitor.update();
        dispose();

        parent.remove(ComponentUtil.getComponentIndex(parent, this));
        parent.revalidate();
        parent.repaint();
    }

    public void  setEnabled(boolean enabled){
        cbType.setEnabled(enabled);
        txtKey.setEnabled(enabled);
        cValue.getComponent().setEnabled(enabled);
        btnRemove.setEnabled(enabled);
    }

    public void dispose(){
        options.getOnChange().run(null);
        cValue.dispose();
    }

    @FunctionalInterface
    public interface OnChange {
        void run(KeyValueItemDto item);
    }
}
