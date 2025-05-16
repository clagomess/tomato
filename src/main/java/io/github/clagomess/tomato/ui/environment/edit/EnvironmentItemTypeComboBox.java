package io.github.clagomess.tomato.ui.environment.edit;

import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum;

import javax.swing.*;

public class EnvironmentItemTypeComboBox extends JComboBox<EnvironmentItemTypeEnum> {
    public EnvironmentItemTypeComboBox() {
        super(EnvironmentItemTypeEnum.values());
    }

    public EnvironmentItemTypeEnum getSelectedItem(){
        return (EnvironmentItemTypeEnum) super.getSelectedItem();
    }
}
