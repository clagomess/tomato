package com.github.clagomess.tomato.ui.environment.edit;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.service.EnvironmentDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImageIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class EnvironmentEditUI extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();

    private final EnvironmentDataService environmentDataService = new EnvironmentDataService();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();
    private final EnvironmentDto environment;

    public EnvironmentEditUI(
            Component parent,
            EnvironmentDto environment
    ){
        this.environment = environment;

        setTitle("Environment - " + environment.getName());
        setIconImage(new FaviconImageIcon().getImage());
        setMinimumSize(new Dimension(600, 400));
        setResizable(true);

        txtName.setText(environment.getName());
        EnvUI envUI = new EnvUI(environment.getEnvs());

        JPanel panel = new JPanel(new MigLayout());
        panel.add(new JLabel("Name"), "wrap");
        panel.add(txtName, "width 100%, wrap");
        panel.add(envUI, "width 100%, height 100%, wrap");
        panel.add(btnSave, "align right");
        add(panel);

        btnSave.addActionListener(l -> btnSaveAction());

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void btnSaveAction(){
        btnSave.setEnabled(false);

        try {
            environment.setName(txtName.getText());

            environmentDataService.save(environment);

            environmentPublisher.getOnSave().publish(
                    environment.getId(),
                    environment
            );
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        } finally {
            btnSave.setEnabled(true);
        }
    }
}
