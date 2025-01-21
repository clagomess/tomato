package com.github.clagomess.tomato.ui.main.request.left.bodytype.multipartform;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import com.github.clagomess.tomato.ui.component.ComponentUtil;
import com.github.clagomess.tomato.ui.component.FileChooser;
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

import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.TEXT;

@Getter
@Setter
class RowComponent extends JPanel {
    private final Container parent;
    private final RequestStagingMonitor requestStagingMonitor;
    private final List<RequestDto.KeyValueItem> multiPartFormItems;
    private final RequestDto.KeyValueItem item;

    private final JComboBox<KeyValueTypeEnum> cbType = new JComboBox<>(
            KeyValueTypeEnum.values()
    );
    private final ListenableTextField txtKey = new ListenableTextField();
    private JComponent cValue;
    private final JCheckBox cbSelected = new JCheckBox();
    private final JButton btnRemove = new JButton(new BxTrashIcon()){{
        setToolTipText("Remove");
    }};

    public RowComponent(
            Container parent,
            RequestStagingMonitor requestStagingMonitor,
            List<RequestDto.KeyValueItem> multiPartFormItems,
            RequestDto.KeyValueItem item
    ){
        this.parent = parent;
        this.requestStagingMonitor = requestStagingMonitor;
        this.multiPartFormItems = multiPartFormItems;
        this.item = item;

        if(!this.multiPartFormItems.contains(this.item)){
            this.multiPartFormItems.add(this.item);
        }

        // set values
        cbType.setSelectedItem(item.getType());
        txtKey.setText(item.getKey());
        cValue = createCValue(item.getType(), item.getValue());
        cbSelected.setSelected(item.isSelected());
        setEnabled(item.isSelected());

        // listeners
        cbType.addActionListener(l -> cbTypeOnChange());
        txtKey.addOnChange(value -> {
            item.setKey(value);
            requestStagingMonitor.update();
        });
        cbSelected.addActionListener(l -> cbSelectedOnChange());
        btnRemove.addActionListener(l -> btnRemoveAction());

        // layout
        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[][][][grow, fill][]"
        ));
        add(cbSelected);
        add(cbType, "width 70!");
        add(txtKey, "width 100!");
        add(cValue, "width 100:100:100%");
        add(btnRemove);
    }

    // @TODO: implement option to fill Content-Type when 'type File'

    private void cbTypeOnChange(){
        item.setType((KeyValueTypeEnum) cbType.getSelectedItem());
        requestStagingMonitor.update();

        int index = ComponentUtil.getComponentIndex(this, cValue);
        remove(index);

        cValue = createCValue(item.getType(), item.getValue());

        add(cValue, "width 100:100:100%", index);
        revalidate();
        repaint();
    }

    private void cbSelectedOnChange(){
        item.setSelected(cbSelected.isSelected());
        requestStagingMonitor.update();
        setEnabled(cbSelected.isSelected());
    }

    private void btnRemoveAction(){
        multiPartFormItems.remove(item);
        requestStagingMonitor.update();

        if(cValue instanceof EnvTextField){
            ((EnvTextField) cValue).dispose();
        }

        parent.remove(ComponentUtil.getComponentIndex(parent, this));
        parent.revalidate();
        parent.repaint();
    }

    public JComponent createCValue(KeyValueTypeEnum type, String value){
        if(type == TEXT){
            var textField = new EnvTextField();
            textField.setText(value);
            textField.addOnChange(vl -> {
                item.setValue(vl);
                requestStagingMonitor.update();
            });
            return textField;
        }else{
            var fileChooser = new FileChooser();
            fileChooser.setValue(value);
            fileChooser.addOnChange(file -> {
                item.setValue(file != null ? file.getAbsolutePath() : null);
                requestStagingMonitor.update();
            });
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
