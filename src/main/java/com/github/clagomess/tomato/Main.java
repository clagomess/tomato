package com.github.clagomess.tomato;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.github.clagomess.tomato.form.MainForm;

import javax.swing.*;

public class Main {
    public static void main(String[] argv){
        FlatDarculaLaf.setup();
        SwingUtilities.invokeLater(() -> new MainForm());
    }
}
