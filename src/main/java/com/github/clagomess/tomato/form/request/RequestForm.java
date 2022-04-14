package com.github.clagomess.tomato.form.request;

import javax.swing.*;

public class RequestForm extends JTabbedPane {
    public RequestForm(){
        addTab("Aoba", getTabContent());
        addTab("Aoba - 2", getTabContent());
    }

    public JPanel getTabContent(){
        JPanel jPanel = new JPanel();
        jPanel.add(new JComboBox<String>());
        jPanel.add(new JTextField());
        jPanel.add(new JButton("Send"));

        return jPanel;
    }
}
