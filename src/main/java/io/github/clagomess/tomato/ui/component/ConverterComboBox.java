package io.github.clagomess.tomato.ui.component;

import io.github.clagomess.tomato.io.converter.InterfaceConverter;
import io.github.clagomess.tomato.io.converter.PostmanConverter;

import javax.swing.*;

public class ConverterComboBox extends JComboBox<InterfaceConverter> {
    public ConverterComboBox() {
        super();
        setRenderer(new DtoListCellRenderer<>(InterfaceConverter::getConverterName));
        addItem(new PostmanConverter());
    }

    @Override
    public InterfaceConverter getSelectedItem() {
        return (InterfaceConverter) super.getSelectedItem();
    }
}
