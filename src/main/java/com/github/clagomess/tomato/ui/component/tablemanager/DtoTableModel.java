package com.github.clagomess.tomato.ui.component.tablemanager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.table.AbstractTableModel;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;

@Getter
@Slf4j
public class DtoTableModel<T> extends AbstractTableModel {
    private final Class<T> clazz;
    private final List<String> columnNames;
    private final Map<String, String> columnNamesAlias;
    private final List<T> data;

    public DtoTableModel(Class<T> clazz, List<T> data){
        this.clazz = clazz;
        this.columnNames = new ArrayList<>(clazz.getDeclaredFields().length);
        this.columnNamesAlias = new HashMap<>();

        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            ModelColumn column = field.getAnnotation(ModelColumn.class);
            String name = column != null ? column.name() : field.getName();
            this.columnNames.add(name);
            this.columnNamesAlias.put(name, field.getName());
        });

        this.data = data;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    private Optional<PropertyDescriptor> getPropertyDescriptor(int columnIndex) throws IntrospectionException {
        return Arrays.stream(
                Introspector.getBeanInfo(clazz, Object.class)
                        .getPropertyDescriptors()
        ).filter(item -> item.getName().equals(columnNamesAlias.get(columnNames.get(columnIndex))))
        .findFirst();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            Optional<PropertyDescriptor> opt = getPropertyDescriptor(columnIndex);

            if (opt.isPresent()) {
                return opt.get().getReadMethod().invoke(data.get(rowIndex));
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        try {
            Optional<PropertyDescriptor> opt = getPropertyDescriptor(column);

            if (opt.isPresent()) {
                opt.get().getWriteMethod().invoke(data.get(row), aValue);
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    public void addRow(T row){
        data.add(row);
        fireTableRowsInserted(data.size()-1, data.size()-1);
    }

    public void clear(){
        int size = data.size();
        if(size == 0) return;
        data.clear();
        fireTableRowsDeleted(0, size-1);
    }
}
