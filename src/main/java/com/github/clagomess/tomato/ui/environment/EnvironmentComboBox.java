package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import com.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;

import javax.swing.*;

import static com.github.clagomess.tomato.ui.component.ColorConstant.RED;

class EnvironmentComboBox extends JComboBox<EnvironmentHeadDto> {
    private static final BxsCircleIcon ICON_PRODUCTION = new BxsCircleIcon(RED, 18);

    public EnvironmentComboBox() {
        setRenderer(new DtoListCellRenderer<EnvironmentHeadDto>((label, value) -> {
            label.setText(value.getName());

            if(value.isProduction()){
                label.setIcon(ICON_PRODUCTION);
            }
        }));
    }

    @Override
    public EnvironmentHeadDto getSelectedItem() {
        return (EnvironmentHeadDto) super.getSelectedItem();
    }
}
