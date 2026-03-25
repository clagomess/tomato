package io.github.clagomess.tomato;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.util.SystemInfo;
import io.github.clagomess.tomato.ui.MainFrame;
import io.github.clagomess.tomato.ui.MainSingleInstance;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class Main {
    private static final MainSingleInstance singleInstance = new MainSingleInstance();

    public static void main(String[] argv){
        log.info("Starting...");
        if(singleInstance.alreadyStarted()) return;

        if (SystemInfo.isMacOS) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.appearance", "system");
            System.setProperty("apple.awt.application.name", "Tomato");
        }

        FlatDarculaLaf.registerCustomDefaultsSource( "io.github.clagomess.tomato.ui" );
        FlatDarculaLaf.setup();

        SwingUtilities.invokeLater(() -> {
            var mainFrame = new MainFrame();
            singleInstance.setBringToFront(() ->
                    SwingUtilities.invokeLater(mainFrame::toFront)
            );
        });
    }
}
