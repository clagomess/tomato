package io.github.clagomess.tomato.ui.environment;

import io.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import io.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;

import javax.swing.*;

import static io.github.clagomess.tomato.ui.component.ColorConstant.RED;

class EnvironmentComboBox extends JComboBox<EnvironmentHeadDto> {
    private static final Icon PRODUCTION_ICON = new BxsCircleIcon(RED, 18);

    public EnvironmentComboBox() {
        setRenderer(new DtoListCellRenderer<EnvironmentHeadDto>((label, value) -> {
            label.setText(value.getName());

            if(value.isProduction()){
                label.setIcon(PRODUCTION_ICON);
            }
        }));
    }

    @Override
    public EnvironmentHeadDto getSelectedItem() {
        return (EnvironmentHeadDto) super.getSelectedItem();
    }
}
