package io.github.clagomess.tomato.ui.component;

import io.github.clagomess.tomato.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class WaitExecution {
    public static final Executor EDT = SwingUtilities::invokeLater;
    public static final Executor BACKGROUND = ForkJoinPool.commonPool();

    private final Component component;
    private final JButton button;

    private record Step(Executor executor, Task task) {}
    private final List<Step> steps = new ArrayList<>();

    public WaitExecution(Component component, JButton button, Task task) {
        this.component = component;
        this.button = button;
        thenEdt(task);
    }

    public WaitExecution(Component component, Task task) {
        this(component, null, task);
    }

    public WaitExecution(Task task) {
        this(null, task);
    }

    public WaitExecution thenBackground(Task task){
        steps.add(new Step(BACKGROUND, task));
        return this;
    }

    public WaitExecution thenEdt(Task task){
        steps.add(new Step(EDT, task));
        return this;
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

    public CompletableFuture<Void> execute(){
        var result = CompletableFuture.runAsync(() -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            setButtonEnabled(false);
        }, EDT);

        for(var step : steps){
            result = result.thenRunAsync(() -> {
                try {
                    step.task().run();
                } catch (Exception e) {
                    throw new CompletionException(e.getMessage(), e);
                }
            }, step.executor());
        }

        result.whenCompleteAsync((v, e) -> {
            if(e != null) new ExceptionDialog(component, e);
            setButtonEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }, EDT);

        return result;
    }

    @FunctionalInterface
    public interface Task {
        void run() throws Exception;
    }
}
