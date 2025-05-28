package io.github.clagomess.tomato.ui.component.tablemanager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import javax.swing.table.DefaultTableCellRenderer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TableManagerTest {
    static class FooRenderer extends DefaultTableCellRenderer {}

    @Getter
    @Setter
    @AllArgsConstructor
    static class FooDto {
        @ModelColumn(name = "A")
        private String a;

        @ModelColumn(name = "B", width = 100)
        private String b;

        @ModelColumn(name = "C", cellRenderer = FooRenderer.class)
        private String c;

        private String d;
    }

    @Test
    void isCellEditable(){
        TableManager<FooDto> tableManager = new TableManager<>(FooDto.class);
        tableManager.getModel().addRow(new FooDto("A", "B", "C", "D"));

        assertTrue(tableManager.getTable().isCellEditable(0,0));
        assertTrue(tableManager.getTable().isCellEditable(0,1));
        assertFalse(tableManager.getTable().isCellEditable(0,2));
        assertTrue(tableManager.getTable().isCellEditable(0,3));
    }
}
