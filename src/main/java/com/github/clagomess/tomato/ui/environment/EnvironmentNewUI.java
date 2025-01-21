package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class EnvironmentNewUI extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();

    private final EnvironmentRepository environmentRepository = new EnvironmentRepository();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();

    public EnvironmentNewUI(Component parent){
        setTitle("New Environment");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("Name"), "wrap");
        add(txtName, "width 300!, wrap");
        add(btnSave, "align right");

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnSave.addActionListener(l -> btnSaveAction());
    }

    private void btnSaveAction(){
        new WaitExecution(this, btnSave, () -> {
            EnvironmentDto environment = new EnvironmentDto();
            environment.setName(txtName.getText());

            environmentRepository.save(environment);
            environmentPublisher.getOnInsert().publish(environment.getId());

            setVisible(false);
            dispose();
        }).execute();
    }
}
