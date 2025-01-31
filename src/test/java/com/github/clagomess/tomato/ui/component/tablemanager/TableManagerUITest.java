package com.github.clagomess.tomato.ui.component.tablemanager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import javax.swing.table.DefaultTableCellRenderer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TableManagerUITest {
    public static class FooRenderer extends DefaultTableCellRenderer {}

    @Getter
    @Setter
    @AllArgsConstructor
    public static class FooDto {
        @ModelColumn(name = "A")
        private String a;

        @ModelColumn(name = "B", width = 100)
        private String b;

        @ModelColumn(name = "C", cellRenderer = FooRenderer.class)
        private String c;

        private String d;
    }

    @Test
    public void isCellEditable(){
        TableManagerUI<FooDto> tableManagerUI = new TableManagerUI<>(FooDto.class);
        tableManagerUI.getModel().addRow(new FooDto("A", "B", "C", "D"));

        assertTrue(tableManagerUI.getTable().isCellEditable(0,0));
        assertTrue(tableManagerUI.getTable().isCellEditable(0,1));
        assertFalse(tableManagerUI.getTable().isCellEditable(0,2));
        assertTrue(tableManagerUI.getTable().isCellEditable(0,3));
    }
}
