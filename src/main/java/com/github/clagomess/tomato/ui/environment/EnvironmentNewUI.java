package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.service.EnvironmentDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImageIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class EnvironmentNewUI extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final JTextField txtName = new JTextField();

    private final EnvironmentDataService environmentDataService = EnvironmentDataService.getInstance();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();

    public EnvironmentNewUI(Component parent){
        setTitle("New Environment");
        setIconImage(new FaviconImageIcon().getImage());
        setMinimumSize(new Dimension(300, 100));
        setResizable(false);

        JPanel panel = new JPanel(new MigLayout());
        panel.add(new JLabel("Name"), "wrap");
        panel.add(txtName, "width 100%, wrap");
        panel.add(btnSave, "align right");
        add(panel);

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnSave.addActionListener(l -> btnSaveAction());
    }

    private void btnSaveAction(){
        btnSave.setEnabled(false);

        try {
            EnvironmentDto environment = new EnvironmentDto();
            environment.setName(txtName.getText());

            environmentDataService.save(environment);
            environmentPublisher.getOnInsert().publish(environment.getId());

            setVisible(false);
            dispose();
        } catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        } finally {
            btnSave.setEnabled(true);
        }
    }
}
