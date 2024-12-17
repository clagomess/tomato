package com.github.clagomess.tomato.ui.component;

import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;

@RequiredArgsConstructor
public class WaitExecution {
    private final Component component;
    private final JButton button;
    private Task execute;
    private Runnable onFinish;

    public WaitExecution setExecute(Task execute) {
        this.execute = execute;
        return this;
    }

    public WaitExecution setOnFinish(Runnable onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    public void execute(){
        SwingUtilities.invokeLater(() -> {
            try {
                component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                if(button != null) button.setEnabled(false);
                execute.run();
            } catch (Throwable e){
                DialogFactory.createDialogException(component, e);
            } finally {
                if(button != null) button.setEnabled(true);
                if(onFinish != null) onFinish.run();
                component.setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    @FunctionalInterface
    public interface Task {
        void run() throws Throwable;
    }
}
