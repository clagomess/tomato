package com.github.clagomess.tomato.ui.main.request.keyvalue;

import com.github.clagomess.tomato.dto.data.KeyValueItemDto;
import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import com.github.clagomess.tomato.ui.component.ComponentUtil;
import com.github.clagomess.tomato.ui.component.FileChooser;
import com.github.clagomess.tomato.ui.component.IconButton;
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

import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.TEXT;

@Getter
@Setter
class RowComponent extends JPanel {
    private final Container parent;
    private final RequestStagingMonitor requestStagingMonitor;
    private final List<KeyValueItemDto> listItens;
    private final KeyValueItemDto item;
    private KeyValueOptions options;

    private final JComboBox<KeyValueTypeEnum> cbType = new JComboBox<>(
            KeyValueTypeEnum.values()
    );
    private final ListenableTextField txtKey = new ListenableTextField();
    private JComponent cValue;
    private final JCheckBox cbSelected = new JCheckBox();
    private final JButton btnRemove = new IconButton(new BxTrashIcon(), "Remove");

    public RowComponent(
            Container parent,
            RequestStagingMonitor requestStagingMonitor,
            List<KeyValueItemDto> listItens,
            KeyValueItemDto item,
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

        // set values
        cbType.setSelectedItem(item.getType());
        txtKey.setText(item.getKey());
        cValue = createCValue(item.getType(), item.getValue());
        cbSelected.setSelected(item.isSelected());
        setEnabled(item.isSelected());

        // listeners
        cbSelected.addActionListener(l -> cbSelectedOnChange());
        cbType.addActionListener(l -> cbTypeOnChange());
        txtKey.addOnChange(this::txtKeyOnChange);
        btnRemove.addActionListener(l -> btnRemoveAction());

        // layout
        setLayout(new MigLayout(
                "insets 2",
                options.isEnableTypeColumn() ?
                        "[][][][grow, fill][]" :
                        "[][][grow, fill][]"
        ));
        add(cbSelected);

        if(options.isEnableTypeColumn()) {
            add(cbType, "width 70!");
        }

        add(txtKey, "width 100!");
        add(cValue, "width 100:100:100%");
        add(btnRemove);
    }

    // @TODO: implement option to fill Content-Type when 'type File'

    private void cbTypeOnChange(){
        KeyValueTypeEnum selectedType = (KeyValueTypeEnum) cbType.getSelectedItem();
        if(Objects.equals(selectedType, item.getType())) return;

        item.setType(selectedType);
        requestStagingMonitor.update();

        int index = ComponentUtil.getComponentIndex(this, cValue);
        remove(index);

        cValue = createCValue(item.getType(), item.getValue());

        add(cValue, "width 100:100:100%", index);
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

    private void valueOnChange(String value){
        if(Objects.equals(value, item.getValue())) return;

        item.setValue(value);
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

    private JComponent createCValue(KeyValueTypeEnum type, String value){
        if(type == TEXT || !options.isEnableTypeColumn()) {
            if(options.getEnvTextfieldOptions().isValueEditorShowContentTypeEdit()) {
                options.getEnvTextfieldOptions().setValueEditorSelectedRawBodyType(
                        RawBodyTypeEnum.valueOfContentType(item.getValueContentType())
                );
                options.getEnvTextfieldOptions().setValueEditorOnDispose((rawBodyType, text) -> {
                    if(Objects.equals(
                            item.getValueContentType(),
                            rawBodyType.getContentType().toString()
                    )) return;

                    options.getEnvTextfieldOptions().setValueEditorSelectedRawBodyType(rawBodyType);
                    item.setValueContentType(rawBodyType.getContentType().toString());
                    requestStagingMonitor.update();
                    options.getOnChange().run(item);
                });
            }

            var textField = new EnvTextField(options.getEnvTextfieldOptions());
            textField.setText(value);
            textField.addOnChange(this::valueOnChange);
            return textField;
        }else{
            var fileChooser = new FileChooser();
            fileChooser.setValue(value);
            fileChooser.addOnChange(file -> {
                valueOnChange(file != null ? file.getAbsolutePath() : null);
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

    public void dispose(){
        options.getOnChange().run(null);
        if(cValue instanceof EnvTextField envTextField){
            envTextField.dispose();
        }
    }

    @FunctionalInterface
    public interface OnChange {
        void run(KeyValueItemDto item);
    }
}
