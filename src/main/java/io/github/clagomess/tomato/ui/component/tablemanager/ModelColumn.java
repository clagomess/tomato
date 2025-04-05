package io.github.clagomess.tomato.ui.component.tablemanager;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelColumn {
    String name() default "";
    int width() default 0;
    Class<? extends TableCellRenderer> cellRenderer() default DefaultTableCellRenderer.class;
}
