package com.github.clagomess.tomato.ui.environment.edit;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.ui.component.ListenableTextField;
import com.github.clagomess.tomato.ui.component.StagingMonitor;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static javax.swing.SwingUtilities.invokeLater;

public class EnvironmentEditFrame extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final ListenableTextField txtName = new ListenableTextField();
    private final KeyValue keyValue;
    private final StagingMonitor<EnvironmentDto> stagingMonitor;
    private final String title;

    private final EnvironmentRepository environmentRepository = new EnvironmentRepository();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();

    @Getter
    private final EnvironmentDto environment;

    public EnvironmentEditFrame(
            Component parent,
            String environmentId
    ) throws IOException {
        this.environment = environmentRepository.load(environmentId).orElseThrow();
        this.stagingMonitor = new StagingMonitor<>(environment);
        this.title = "Environment - " + environment.getName();

        setTitle(title);
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(600, 500));
        setPreferredSize(new Dimension(600, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        txtName.setText(environment.getName());
        txtName.addOnChange(value -> {
            environment.setName(value);
            updateStagingMonitor();
        });
        keyValue = new KeyValue(environment.getEnvs());

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("Name"), "wrap");
        add(txtName, "width 100%, wrap");
        add(keyValue, "width 100%, height 100%, wrap");
        add(btnSave, "align right");

        btnSave.addActionListener(l -> btnSaveAction());

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public void updateStagingMonitor(){
        stagingMonitor.update();

        if(stagingMonitor.isDiferent()){
            invokeLater(() -> setTitle("[*] " + title));
        }else{
            invokeLater(() -> setTitle(title));
        }
    }

    public void resetStagingMonitor(){
        keyValue.resetStagingMonitor();
        stagingMonitor.reset();
        invokeLater(() -> setTitle(title));
    }

    private void btnSaveAction(){
        new WaitExecution(this, btnSave, () -> {
            environmentRepository.save(environment);

            resetStagingMonitor();

            environmentPublisher.getOnChange()
                    .publish(new PublisherEvent<>(UPDATED, environment.getId()));
        }).execute();
    }
}
