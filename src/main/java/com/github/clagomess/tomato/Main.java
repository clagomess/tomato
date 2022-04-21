package com.github.clagomess.tomato;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.github.clagomess.tomato.factory.DialogFactory;
import com.github.clagomess.tomato.service.DataService;
import com.github.clagomess.tomato.ui.MainUI;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static MainUI mainUi;

    public static void main(String[] argv){
        FlatDarculaLaf.setup();

        try {
            DataService.getInstance().startLoad();
        }catch (IOException e){
            DialogFactory.createDialogException(null, e);
        }

        SwingUtilities.invokeLater(() -> mainUi = new MainUI());
    }
}
