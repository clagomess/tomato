package io.github.clagomess.tomato.ui.environment;

import io.github.clagomess.tomato.controller.environment.EnvironmentComboBoxController;
import io.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import io.github.clagomess.tomato.ui.component.ComponentUtil;
import io.github.clagomess.tomato.ui.component.DtoListCellRenderer;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxsCircleIcon;

import javax.swing.*;
import java.io.IOException;

import static io.github.clagomess.tomato.ui.component.ColorConstant.RED;

class EnvironmentComboBox
        extends JComboBox<EnvironmentHeadDto>
        implements EnvironmentComboBoxInterface {
    private static final Icon PRODUCTION_ICON = new BxsCircleIcon(RED, 18);
    private final boolean addEmptyOption;

    private final EnvironmentComboBoxController controller;

    public EnvironmentComboBox() {
        this(false);
    }

    public EnvironmentComboBox(boolean addEmptyOption) {
        this.addEmptyOption = addEmptyOption;
        this.controller = new EnvironmentComboBoxController(this);

        setRenderer(new DtoListCellRenderer<EnvironmentHeadDto>((label, value) -> {
            label.setText(value.getName());

            if(value.isProduction()){
                label.setIcon(PRODUCTION_ICON);
            }
        }));

        new WaitExecution(this, this::loadItems).execute();
    }

    public void loadItems() throws IOException {
        ComponentUtil.checkIsEventDispatchThread();
        removeAllItems();
        if (addEmptyOption) addItem(null);
        controller.loadItems();
    }

    @Override
    public EnvironmentHeadDto getSelectedItem() {
        return (EnvironmentHeadDto) super.getSelectedItem();
    }

    @Override
    public void setSelectedItem(EnvironmentHeadDto head) {
        super.setSelectedItem(head);
    }
}
