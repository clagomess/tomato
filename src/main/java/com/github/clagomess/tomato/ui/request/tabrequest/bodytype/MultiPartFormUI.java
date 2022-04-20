package com.github.clagomess.tomato.ui.request.tabrequest.bodytype;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.enums.KeyValueTypeEnum;
import com.github.clagomess.tomato.ui.request.tabrequest.FileChooserUI;
import com.github.clagomess.tomato.util.ComponentUtil;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.FILE;
import static com.github.clagomess.tomato.enums.KeyValueTypeEnum.TEXT;

public class MultiPartFormUI extends JPanel {
    private final List<ComponentRowDto> componentRowList = new ArrayList<>();
    private final JButton btnAddNew = new JButton("Add New");

    public MultiPartFormUI(){
        setLayout(new MigLayout("insets 0 0 0 0", "[grow, fill]", ""));

        add(new JLabel("Type"));
        add(new JLabel("Key"), "width 120:120:120");
        add(new JLabel("Value"), "width 100%");
        add(new JLabel());
        add(new JLabel(), "wrap");

        btnAddNew.addActionListener(l -> addRow(new RequestDto.MultiPartFormItem()));

        //@TODO: must be removed
        addRow(new RequestDto.MultiPartFormItem());

        RequestDto.MultiPartFormItem vrau = new RequestDto.MultiPartFormItem();
        vrau.setType(FILE);
        addRow(vrau);
    }

    private void cbTypeChangeAction(ComponentRowDto componentDto){
        int index = ComponentUtil.getComponentIndex(this, componentDto.getCValue());
        if(index == -1) return;

        componentDto.setCValue();

        remove(index);
        add(componentDto.getCValue(), index);
        revalidate();
        repaint();
    }

    private void addRow(RequestDto.MultiPartFormItem item){
        // remove add button
        int index = ComponentUtil.getComponentIndex(this, btnAddNew);
        if(index != -1) remove(index);

        ComponentRowDto componentDto = new ComponentRowDto(item);
        componentRowList.add(componentDto);

        componentDto.getCbType().addActionListener(l -> cbTypeChangeAction(componentDto));
        componentDto.getCbSelected().addActionListener(l -> componentDto.setEnabled(componentDto.getCbSelected().isSelected()));
        componentDto.getBtnRemove().addActionListener(l -> {}); //@TODO: implements remove action
        addRowLayout(componentDto);
    }

    private void addRowLayout(ComponentRowDto componentDto){
        add(componentDto.getCbType());
        add(componentDto.getTxtKey());
        add(componentDto.getCValue());
        add(componentDto.getCbSelected());
        add(componentDto.getBtnRemove(), "wrap");
        add(btnAddNew, "span"); //@TODO: verificar scrolllayout
    }

    @Getter
    @Setter
    private static class ComponentRowDto {
        private JComboBox<KeyValueTypeEnum> cbType;
        private JTextField txtKey = new JTextField();
        private JComponent cValue;
        private JCheckBox cbSelected = new JCheckBox();
        private JButton btnRemove = new JButton("x"); //@TODO: change to trash icon;

        public ComponentRowDto(RequestDto.MultiPartFormItem item){
            cbType = new JComboBox<>();
            cbType.addItem(TEXT);
            cbType.addItem(FILE);
            cbType.setSelectedItem(item.getType());

            setCValue();
            cbSelected.setSelected(true);
        }

        public void setCValue(){
            cValue = cbType.getSelectedItem() == TEXT ? new JTextField() : new FileChooserUI();
        }

        public void setEnabled(boolean enabled){
            cbType.setEnabled(enabled);
            txtKey.setEnabled(enabled);
            cValue.setEnabled(enabled);
            btnRemove.setEnabled(enabled);
        }
    }
}
