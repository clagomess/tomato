package com.github.clagomess.tomato.ui.main.request.tabrequest;

import com.github.clagomess.tomato.dto.RequestDto;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

@Getter
public class RequestKeyValueTableUI extends JPanel {
    private final DefaultTableModel model = new DefaultTableModel();
    private final JButton btnAddNew = new JButton("Add New");

    public RequestKeyValueTableUI(String keyColumnLabel, String valueColumnLabel){
        setLayout(new MigLayout("insets 0 0 0 0"));

        JScrollPane sp = new JScrollPane();
        JTable table = new JTable(model){
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };
        table.setFocusable(false);
        table.setShowGrid(true);
        sp.setViewportView(table);

        // layout
        add(sp, "width 100%, wrap");
        add(btnAddNew, "align right");

        // columns
        model.addColumn("Enabled?");
        model.addColumn(keyColumnLabel);
        model.addColumn(valueColumnLabel);
        table.getColumnModel().getColumn(0).setMaxWidth(100);

        // rows test //@TODO: must be removed
        addRow(new RequestDto.KeyValueItem("aaaa", "bbbb"));

        btnAddNew.addActionListener(l -> addRow(new RequestDto.KeyValueItem()));
    }

    public void addRow(RequestDto.KeyValueItem item){
        model.addRow(new Object[]{
                item.isSelected(),
                item.getKey(),
                item.getValue()
        });
    }

    public List<RequestDto.KeyValueItem> getNewListDtoFromUI(){
        //@TODO: implements
        return null;
    }
}
