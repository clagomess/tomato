package com.github.clagomess.tomato;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.github.clagomess.tomato.ui.MainUI;

import javax.swing.*;

public class Main {
    public static void main(String[] argv){
        FlatDarculaLaf.registerCustomDefaultsSource( "com.github.clagomess.tomato.ui" );
        FlatDarculaLaf.setup();
        SwingUtilities.invokeLater(MainUI::new);
    }
}
