package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import com.github.clagomess.tomato.ui.component.DtoListCellRenderer;

import javax.swing.*;

class EnvironmentComboBox extends JComboBox<EnvironmentHeadDto> {
    public EnvironmentComboBox() {
        setRenderer(new DtoListCellRenderer<>(EnvironmentHeadDto::getName));
    }

    @Override
    public EnvironmentHeadDto getSelectedItem() {
        return (EnvironmentHeadDto) super.getSelectedItem();
    }
}
