package com.github.clagomess.tomato.ui.main.request.left.bodytype.multipartform;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import com.github.clagomess.tomato.ui.component.ComponentUtil;
import com.github.clagomess.tomato.ui.component.FileChooser;
import com.github.clagomess.tomato.ui.component.ListenableTextField;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTrashIcon;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.TEXT;

@Getter
@Setter
class RowComponent extends JPanel {
    private final Container parent;
    private final RequestDto requestDto;
    private final RequestDto.KeyValueItem item;

    private final JComboBox<KeyValueTypeEnum> cbType = new JComboBox<>(
            KeyValueTypeEnum.values()
    );
    private final ListenableTextField txtKey = new ListenableTextField();
    private JComponent cValue;
    private final JCheckBox cbSelected = new JCheckBox();
    private final JButton btnRemove = new JButton(new BxTrashIcon());

    public RowComponent(
            Container parent,
            RequestDto requestDto,
            RequestDto.KeyValueItem item
    ){
        this.parent = parent;
        this.requestDto = requestDto;
        this.item = item;

        if(!this.requestDto.getBody().getMultiPartForm().contains(this.item)){
            this.requestDto.getBody().getMultiPartForm().add(this.item);
        }

        // set values
        cbType.setSelectedItem(item.getType());
        txtKey.setText(item.getKey());
        cValue = createCValue(item.getType(), item.getValue());
        cbSelected.setSelected(item.isSelected());

        // listeners
        cbType.addActionListener(l -> cbTypeOnChange());
        txtKey.addOnChange(item::setKey);
        cbSelected.addActionListener(l -> cbSelectedOnChange());
        btnRemove.addActionListener(l -> btnRemoveAction());

        // layout
        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[][][][grow, fill][]",
                ""
        ));
        add(cbSelected, "width 25:25:25");
        add(cbType, "width 70:70:70");
        add(txtKey, "width 100:100:100");
        add(cValue, "width 100%");
        add(btnRemove, "wrap");
    }

    private void cbTypeOnChange(){
        item.setType((KeyValueTypeEnum) cbType.getSelectedItem());

        int index = ComponentUtil.getComponentIndex(this, cValue);
        remove(index);

        cValue = createCValue(item.getType(), item.getValue());

        add(cValue, index);
        revalidate();
        repaint();
    }

    private void cbSelectedOnChange(){
        item.setSelected(cbSelected.isSelected());
        setEnabled(cbSelected.isSelected());
    }

    private void btnRemoveAction(){
        requestDto.getBody().getMultiPartForm().remove(item);
        parent.remove(ComponentUtil.getComponentIndex(parent, this));
        parent.revalidate();
        parent.repaint();
    }

    public JComponent createCValue(KeyValueTypeEnum type, String value){
        if(type == TEXT){
            var textField = new ListenableTextField();
            textField.setText(value);
            textField.addOnChange(item::setValue);
            return textField;
        }else{
            var fileChooser = new FileChooser();
            fileChooser.setValue(value);
            fileChooser.addOnChange(file -> item.setValue(file.getAbsolutePath()));
            return fileChooser;
        }
    }

    public void setEnabled(boolean enabled){
        cbType.setEnabled(enabled);
        txtKey.setEnabled(enabled);
        cValue.setEnabled(enabled);
        btnRemove.setEnabled(enabled);
    }
}
