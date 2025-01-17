package com.github.clagomess.tomato;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.github.clagomess.tomato.ui.MainUI;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class Main {
    public static void main(String[] argv){
        log.info("Starting...");

        FlatDarculaLaf.registerCustomDefaultsSource( "com.github.clagomess.tomato.ui" );
        FlatDarculaLaf.setup();

        SwingUtilities.invokeLater(() -> {
            new MainUI();
            log.info("Started!");
        });
    }
}
