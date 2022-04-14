package com.github.clagomess.tomato;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.github.clagomess.tomato.form.MainForm;

public class Main {
    public static void main(String[] argv){
        FlatDarculaLaf.setup();
        new MainForm();
    }
}
