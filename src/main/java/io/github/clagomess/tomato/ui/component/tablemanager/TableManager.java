package io.github.clagomess.tomato.ui.component.tablemanager;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

@Getter
@Setter
public class TableManager<T> {
    private final DtoTableModel<T> model;
    private final JTable table;

    public TableManager(Class<T> clazz) {
        this(clazz, Collections.emptyList());
    }

    public TableManager(
            Class<T> clazz,
            List<TableCellRenderer> cellRenderer
    ) {
        this.model = new DtoTableModel<>(clazz, new LinkedList<>());
        this.table = new CustomJTable<>(model, cellRenderer);
    }

    private static class CustomJTable<T> extends JTable {
        private final DtoTableModel<T> model;
        private final List<TableCellRenderer> cellRenderer;

        private CustomJTable(
                DtoTableModel<T> model,
                List<TableCellRenderer> cellRenderer
        ) {
            super(model);
            this.model = model;
            this.cellRenderer = cellRenderer;

            setFocusable(false);
            setShowGrid(true);
            setAutoCreateRowSorter(true);
            setModelProperties();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            Field[] fields = model.getClazz().getDeclaredFields();
            ModelColumn column = fields[col].getAnnotation(ModelColumn.class);

            if(column != null && column.cellRenderer() != DefaultTableCellRenderer.class){
                return false;
            }

            return super.isCellEditable(row, col);
        }

        private void setModelProperties() {
            Field[] fields = model.getClazz().getDeclaredFields();

            IntStream.range(0, fields.length).forEach(i -> {
                ModelColumn column = fields[i].getAnnotation(ModelColumn.class);
                if(column == null) return;

                // set width
                if(column.width() > 0) {
                    var col = getColumnModel().getColumn(i);
                    col.setMinWidth(column.width());
                    col.setMaxWidth(column.width());
                }

                // set render
                if(column.cellRenderer() != DefaultTableCellRenderer.class) {
                    cellRenderer.stream()
                            .filter(item -> item.getClass() == column.cellRenderer())
                            .findFirst()
                            .ifPresent(item -> {
                                var col = getColumn(model.getColumnName(i));
                                col.setCellEditor(null);
                                col.setCellRenderer(item);
                            });
                }
            });
        }
    }
}
