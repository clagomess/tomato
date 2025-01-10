package com.github.clagomess.tomato.ui.environment.edit;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.service.EnvironmentDataService;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
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
            EnvironmentDto environment //@TODO: pass ID instead, causing UI problem on combobox
    ){
        this.environment = environment;

        setTitle("Environment - " + environment.getName());
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(600, 500));
        setPreferredSize(new Dimension(600, 500));
        setResizable(true);

        txtName.setText(environment.getName());
        EnvUI envUI = new EnvUI(environment.getEnvs());

        JPanel panel = new JPanel(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        panel.add(new JLabel("Name"), "wrap");
        panel.add(txtName, "width 100%, wrap");
        panel.add(envUI, "width 100%, height 100%, wrap");
        panel.add(btnSave, "align right");
        add(panel);

        btnSave.addActionListener(l -> btnSaveAction());

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void btnSaveAction(){
        new WaitExecution(this, btnSave, () -> {
            environment.setName(txtName.getText());

            environmentDataService.save(environment);

            environmentPublisher.getOnSave().publish(
                    environment.getId(),
                    environment
            );
        }).execute();
    }
}
