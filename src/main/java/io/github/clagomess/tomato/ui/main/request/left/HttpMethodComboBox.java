package io.github.clagomess.tomato.ui.main.request.left;

import io.github.clagomess.tomato.enums.HttpMethodEnum;
import io.github.clagomess.tomato.ui.component.DtoListCellRenderer;

import javax.swing.*;

public class HttpMethodComboBox extends JComboBox<HttpMethodEnum> {
    public HttpMethodComboBox(){
        super(HttpMethodEnum.values());
        setRenderer(new DtoListCellRenderer<HttpMethodEnum>((label, value) -> {
            label.setText(" ");
            label.setIcon(value.getIcon());
        }));
    }

    @Override
    public HttpMethodEnum getSelectedItem() {
        return (HttpMethodEnum) super.getSelectedItem();
    }
}
