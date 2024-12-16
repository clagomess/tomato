package com.github.clagomess.tomato.ui.component.tablemanager;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

@Getter
@Setter
public class TableManagerUI<T> {
    private final DtoTableModel<T> model;
    private final JTable table;
    private final List<TableCellRenderer> cellRenderer;

    public TableManagerUI(Class<T> clazz) {
        this(clazz, Collections.emptyList());
    }

    public TableManagerUI(Class<T> clazz, List<TableCellRenderer> cellRenderer) {
        this.cellRenderer = cellRenderer;
        this.model = new DtoTableModel<>(clazz, new LinkedList<>());
        this.table = new JTable(model);
        table.setFocusable(false);
        table.setShowGrid(true);
        table.setAutoCreateRowSorter(true);
        setTableColumnsWidth();
    }

    private void setTableColumnsWidth(){
        Field[] fields = model.getClazz().getDeclaredFields();

        IntStream.range(0, fields.length).forEach(i -> {
            if(!fields[i].isAnnotationPresent(ModelColumn.class)) return;
            ModelColumn column = fields[i].getAnnotation(ModelColumn.class);

            // set width
            if(column.width() > 0) {
                table.getColumnModel().getColumn(i).setMinWidth(column.width());
                table.getColumnModel().getColumn(i).setMaxWidth(column.width());
            }

            // set render
            cellRenderer.stream()
                    .filter(item -> item.getClass() == column.cellRenderer())
                    .findFirst()
                    .ifPresent(item -> {
                        table.getColumn(model.getColumnName(i))
                                .setCellRenderer(item);
                    });
        });
    }
}
