package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import com.github.clagomess.tomato.ui.component.undoabletextcomponent.UndoableTextField;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;

public class EnvironmentNewFrame extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final UndoableTextField txtName = new UndoableTextField();
    private final JCheckBox chkProduction = new JCheckBox();

    private final EnvironmentRepository environmentRepository = new EnvironmentRepository();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();

    public EnvironmentNewFrame(Component parent){
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
        add(new JLabel("Production?"), "wrap");
        add(chkProduction, "wrap");
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
            environment.setProduction(chkProduction.isSelected());

            environmentRepository.save(environment);
            environmentPublisher.getOnChange()
                    .publish(new PublisherEvent<>(INSERTED, environment.getId()));

            setVisible(false);
            dispose();
        }).execute();
    }
}
