package com.github.clagomess.tomato.ui.request.tabrequest;

import com.github.clagomess.tomato.dto.RequestDto;
import lombok.Getter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

@Getter
public class RequestKeyValueTableUI extends JScrollPane {
    private final DefaultTableModel model = new DefaultTableModel();

    public RequestKeyValueTableUI(String keyColumnLabel, String valueColumnLabel){
        JTable table = new JTable(model){
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };
        table.setFocusable(false);
        table.setShowGrid(true);

        setViewportView(table);

        // columns
        model.addColumn("Enabled?");
        model.addColumn(keyColumnLabel);
        model.addColumn(valueColumnLabel);
        table.getColumnModel().getColumn(0).setMaxWidth(100);

        // rows test //@TODO: must be removed
        addRow(new RequestDto.KeyValueItem("aaaa", "bbbb"));

    }

    public void addRow(RequestDto.KeyValueItem item){
        model.addRow(new Object[]{
                item.isSelected(),
                item.getKey(),
                item.getValue()
        });
    }
}
