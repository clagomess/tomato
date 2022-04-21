package com.github.clagomess.tomato;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.github.clagomess.tomato.ui.MainUI;

import javax.swing.*;

public class Main {
    public static MainUI mainUi;

    public static void main(String[] argv){
        FlatDarculaLaf.setup();
        SwingUtilities.invokeLater(() -> mainUi = new MainUI());
    }
}
