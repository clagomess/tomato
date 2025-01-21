package com.github.clagomess.tomato.ui.environment.edit;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class EnvironmentEditUI extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();

    private final EnvironmentRepository environmentRepository = new EnvironmentRepository();
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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        txtName.setText(environment.getName());
        EnvUI envUI = new EnvUI(environment.getEnvs());

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("Name"), "wrap");
        add(txtName, "width 100%, wrap");
        add(envUI, "width 100%, height 100%, wrap");
        add(btnSave, "align right");

        btnSave.addActionListener(l -> btnSaveAction());

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void btnSaveAction(){
        new WaitExecution(this, btnSave, () -> {
            environment.setName(txtName.getText());

            environmentRepository.save(environment);

            environmentPublisher.getOnSave().publish(
                    environment.getId(),
                    environment
            );
        }).execute();
    }
}
