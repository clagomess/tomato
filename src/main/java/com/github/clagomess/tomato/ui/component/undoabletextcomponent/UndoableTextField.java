package com.github.clagomess.tomato.ui.component.undoabletextcomponent;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class UndoableTextField extends JTextField {
    public UndoableTextField() {
        UndoManagerConfigurator.configure(this);
    }
}
