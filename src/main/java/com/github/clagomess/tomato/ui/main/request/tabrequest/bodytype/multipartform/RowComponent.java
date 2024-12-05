package com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype.multipartform;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import com.github.clagomess.tomato.ui.component.FileChooserComponent;
import com.github.clagomess.tomato.ui.component.ListenableTextFieldComponent;
import com.github.clagomess.tomato.util.ComponentUtil;
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
    private final RequestDto.MultiPartFormItem item;

    private final JComboBox<KeyValueTypeEnum> cbType = new JComboBox<>(
            KeyValueTypeEnum.values()
    );
    private final JTextField txtKey = new JTextField();
    private JComponent cValue;
    private final JCheckBox cbSelected = new JCheckBox();
    private final JButton btnRemove = new JButton("x"); //@TODO: change to trash icon;

    public RowComponent(
            Container parent,
            RequestDto requestDto,
            RequestDto.MultiPartFormItem item
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
        txtKey.addActionListener(l -> txtKeyOnChange());
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

    private void txtKeyOnChange(){
        item.setKey(txtKey.getText());
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
            var textField = new ListenableTextFieldComponent();
            textField.setText(value);
            textField.addOnChange(item::setValue);
            return textField;
        }else{
            var fileChooser = new FileChooserComponent();
            // @TODO: set file
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
