package io.github.clagomess.tomato;

import com.formdev.flatlaf.FlatDarculaLaf;
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
