package com.github.clagomess.tomato.ui;

import com.github.clagomess.tomato.publisher.SystemPublisher;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Slf4j
public class MainWindowAdapter extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent e) {
        int ret = JOptionPane.showConfirmDialog(
                null,
                "Do you want to exit system?",
                "System Closing",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if(ret != JOptionPane.OK_OPTION) return;
        log.warn("SYSTEM CLOSING");

        SystemPublisher.getInstance()
                .getOnClosing()
                .publish("closing");

        e.getWindow().dispose();
        System.exit(0);
    }
}
