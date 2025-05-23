package io.github.clagomess.tomato.ui.component;

import io.github.clagomess.tomato.ui.MainFrame;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

@RequiredArgsConstructor
public class WaitExecution {
    private final Component component;
    private final JButton button;
    private final Task execute;

    public WaitExecution(Component component, Task execute) {
        this(component, null, execute);
    }

    public WaitExecution(Task execute) {
        this(null, null, execute);
    }

    private void setCursor(Cursor cursor) {
        if(component == null) {
            Arrays.stream(Window.getWindows())
                    .filter(item -> item instanceof MainFrame)
                    .findFirst()
                    .ifPresent(window -> window.setCursor(cursor))
            ;
        }else{
            component.setCursor(cursor);
        }
    }

    private void setButtonEnabled(boolean enabled) {
        if(button == null) return;
        button.setEnabled(enabled);
    }

    public void execute(){
        SwingUtilities.invokeLater(() -> {
            try {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                setButtonEnabled(false);
                execute.run();
            } catch (Exception e){
                new ExceptionDialog(component, e);
            } finally {
                setButtonEnabled(true);
                setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    @FunctionalInterface
    public interface Task {
        void run() throws Exception;
    }
}
