package io.github.clagomess.tomato.ui.component;

import io.github.clagomess.tomato.ui.component.undoabletextcomponent.UndoableTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.LinkedList;
import java.util.List;

public class ListenableTextField extends UndoableTextField {
    private final List<OnChangeFI> onChangeList = new LinkedList<>();

    public ListenableTextField() {
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}

            public void update(){
                onChangeList.forEach(ch -> ch.change(getText()));
            }
        });
    }

    public void addOnChange(OnChangeFI value){
        onChangeList.add(value);
    }

    @FunctionalInterface
    public interface OnChangeFI {
        void change(String content);
    }
}
