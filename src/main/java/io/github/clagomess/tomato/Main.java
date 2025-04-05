package io.github.clagomess.tomato;

import com.formdev.flatlaf.FlatDarculaLaf;
import io.github.clagomess.tomato.ui.MainFrame;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class Main {
    public static void main(String[] argv){
        log.info("Starting...");

        FlatDarculaLaf.registerCustomDefaultsSource( "io.github.clagomess.tomato.ui" );
        FlatDarculaLaf.setup();

        SwingUtilities.invokeLater(() -> {
            new MainFrame();
            log.info("Started!");
        });
    }
}
