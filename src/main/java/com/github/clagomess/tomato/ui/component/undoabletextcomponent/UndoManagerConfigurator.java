package com.github.clagomess.tomato.ui.component.undoabletextcomponent;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_Z;
import static javax.swing.event.DocumentEvent.EventType.CHANGE;

@Slf4j
final class UndoManagerConfigurator {
    private static final KeyStroke UNDO_KEY_STROKE = KeyStroke.getKeyStroke(VK_Z, CTRL_DOWN_MASK);
    private static final String MAP_KEY_UNDO_KEY_STROKE = "undoKeyStroke";

    static void configure(JTextComponent textComponent) {
        var undoManager = new UndoManager();

        textComponent.getDocument().addUndoableEditListener(e -> {
            var event = (AbstractDocument.DefaultDocumentEvent) e.getEdit();
            if (event.getType() != CHANGE) {
                undoManager.addEdit(e.getEdit());
            }
        });

        textComponent.getInputMap().put(UNDO_KEY_STROKE, MAP_KEY_UNDO_KEY_STROKE);
        textComponent.getActionMap().put(MAP_KEY_UNDO_KEY_STROKE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                } catch (CannotUndoException e) {
                    log.warn(e.getMessage(), e);
                }
            }
        });
    }
}
