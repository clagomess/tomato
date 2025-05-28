package io.github.clagomess.tomato.ui.environment.edit;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.exception.TomatoException;
import io.github.clagomess.tomato.io.keystore.EnvironmentKeystore;
import io.github.clagomess.tomato.io.repository.EnvironmentRepository;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.ui.component.*;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum.SECRET;
import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static javax.swing.SwingUtilities.invokeLater;

public class EnvironmentEditFrame extends JFrame {
    private final JButton btnSave = new JButton("Save");
    private final ListenableTextField txtName = new ListenableTextField();
    private final JCheckBox chkProduction = new JCheckBox();
    private final KeyValue keyValue;
    private final StagingMonitor<EnvironmentDto> stagingMonitor;
    private final String title;

    private final EnvironmentRepository environmentRepository = new EnvironmentRepository();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();
    private final EnvironmentKeystore environmentKeystore;

    @Getter
    private final EnvironmentDto environment;

    public EnvironmentEditFrame(
            Component parent,
            String environmentId
    ) throws IOException {
        this.environment = environmentRepository.load(environmentId).orElseThrow();
        this.environmentKeystore = getEnvironmentKeystore(environmentId);
        this.stagingMonitor = new StagingMonitor<>(environment);
        this.title = "Environment - " + environment.getName();

        setTitle(title);
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(600, 500));
        setPreferredSize(new Dimension(600, 500));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        txtName.setText(environment.getName());
        chkProduction.setSelected(environment.isProduction());
        txtName.addOnChange(value -> {
            environment.setName(value);
            updateStagingMonitor();
        });
        chkProduction.addActionListener(listener -> {
            environment.setProduction(chkProduction.isSelected());
            updateStagingMonitor();
        });
        keyValue = new KeyValue(
                environmentKeystore,
                environment.getEnvs()
        );

        setLayout(new MigLayout(
                "insets 10",
                "[][grow, fill]"
        ));
        add(new JLabel("Name:"));
        add(txtName, "wrap");
        add(new JLabel("Production?:"));
        add(chkProduction, "wrap");
        add(keyValue, "span 2, width 100%, height 100%, wrap");
        add(btnSave, "span 2, align right");

        btnSave.addActionListener(l -> btnSaveAction());

        getRootPane().setDefaultButton(btnSave);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private EnvironmentKeystore getEnvironmentKeystore(String environmentId) {
        try {
            File workspacePath = new WorkspaceRepository()
                    .getDataSessionWorkspace()
                    .getPath();

            var environmentKeystore = new EnvironmentKeystore(workspacePath, environmentId);
            environmentKeystore.setGetPassword(() -> new PasswordDialog(this).showDialog());
            environmentKeystore.setGetNewPassword(() -> new NewPasswordDialog(this).showDialog());

            return environmentKeystore;
        }catch (IOException e){
            throw new TomatoException(e);
        }
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
            List<EnvironmentKeystore.Entry> secretsToSave = environment.getEnvs().stream()
                    .filter(item -> item.getType().equals(SECRET))
                    .filter(item -> item.getValue() != null)
                    .map(EnvironmentKeystore.Entry::new)
                    .toList();

            environmentKeystore.saveEntries(secretsToSave).forEach(entry ->
                environment.getEnvs().stream()
                        .filter(env -> env.getKey().equals(entry.getKey()))
                        .findFirst()
                        .ifPresent(env -> {
                            env.setSecretId(entry.getEntryId());
                            env.setValue(null);
                        })
            );

            environmentRepository.save(environment);

            resetStagingMonitor();

            environmentPublisher.getOnChange()
                    .publish(new PublisherEvent<>(UPDATED, environment.getId()));
        }).execute();
    }
}
