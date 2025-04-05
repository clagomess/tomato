package io.github.clagomess.tomato.ui.component;

import javax.swing.*;
import java.awt.*;

public class DtoListCellRenderer<T> extends DefaultListCellRenderer {
    private final ValueParseFI<T> valueParseFI;
    private final LabelChangeFI<T> labelChangeFI;

    public DtoListCellRenderer(ValueParseFI<T> valueParseFI) {
        super();
        this.valueParseFI = valueParseFI;
        this.labelChangeFI = null;
    }

    public DtoListCellRenderer(LabelChangeFI<T> labelChangeFI) {
        super();
        this.valueParseFI = null;
        this.labelChangeFI = labelChangeFI;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
        );

        if(value == null){
            label.setText("empty");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setEnabled(false);
            return label;
        }

        if(valueParseFI != null) {
            label.setText(valueParseFI.parse((T) value));
        }else{
            labelChangeFI.change(label, (T) value);
        }

        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setEnabled(true);

        return label;
    }

    @FunctionalInterface
    public interface ValueParseFI<T> {
        String parse(T value);
    }

    @FunctionalInterface
    public interface LabelChangeFI<T> {
        void change(JLabel label, T value);
    }
}
