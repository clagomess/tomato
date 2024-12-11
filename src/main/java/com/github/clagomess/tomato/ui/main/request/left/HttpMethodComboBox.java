package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.enums.HttpMethodEnum;

import javax.swing.*;
import java.awt.*;

public class HttpMethodComboBox extends JComboBox<HttpMethodEnum> {
    public HttpMethodComboBox(){
        super(HttpMethodEnum.values());
        setRenderer(new IconRenderer());
    }

    @Override
    public HttpMethodEnum getSelectedItem() {
        return (HttpMethodEnum) super.getSelectedItem();
    }

    protected static class IconRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            if(value == null) return new JLabel("Empty");
            return new JLabel(" ", ((HttpMethodEnum) value).getIcon(), SwingConstants.LEFT);
        }
    }
}
