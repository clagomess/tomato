package io.github.clagomess.tomato.ui;

import io.github.clagomess.tomato.publisher.SystemPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Slf4j
@RequiredArgsConstructor
public class MainWindowAdapter extends WindowAdapter {
    private final MainFrame mainFrame;

    public static boolean shouldQuit(Component parent){
        int ret = JOptionPane.showConfirmDialog(
                parent,
                "Do you want to exit system?",
                "System Closing",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if(ret != JOptionPane.OK_OPTION) return false;
        log.warn("SYSTEM CLOSING");

        SystemPublisher.getInstance()
                .getOnClosing()
                .publish("closing");

        return true;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if(!shouldQuit(mainFrame)) return;
        e.getWindow().dispose();
        System.exit(0);
    }
}
