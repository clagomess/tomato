package com.github.clagomess.tomato.factory;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class DialogFactory {
    public static void createDialogException(Component parent, Throwable e){
        log.error(DialogFactory.class.getName(), e);
        JOptionPane.showMessageDialog(
                parent,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void createDialogWarning(Component parent, String message){
        JOptionPane.showMessageDialog(
                parent,
                message,
                "Warning",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
