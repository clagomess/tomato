package com.github.clagomess.tomato;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.github.clagomess.tomato.ui.MainUi;

import javax.swing.*;

public class Main {
    public static void main(String[] argv){
        FlatDarculaLaf.setup();
        SwingUtilities.invokeLater(() -> new MainUi());
    }
}
